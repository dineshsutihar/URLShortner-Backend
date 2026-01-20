package me.dineshsutihar.urlshortner.controller;

import me.dineshsutihar.urlshortner.entity.ShortUrl;
import me.dineshsutihar.urlshortner.service.UrlService;
import me.dineshsutihar.urlshortner.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final JwtTokenProvider jwtTokenProvider;

    // Public: shorten URL (unauthenticated)
    @PostMapping("/api/url/public")
    public ResponseEntity<?> createPublicShortUrl(@RequestBody Map<String, String> body) {
        String originalUrl = body.get("url");
        if (originalUrl == null || originalUrl.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "URL is required"));
        }
        String shortCode = urlService.createPublicShortUrl(originalUrl);
        return ResponseEntity.ok(Map.of("shortCode", shortCode));
    }

    // Private: shorten URL (authenticated)
    @PostMapping("/api/url/private")
    public ResponseEntity<?> createPrivateShortUrl(HttpServletRequest request,
                                                    @RequestBody Map<String, String> body) {
        String email = extractEmail(request);
        String originalUrl = body.get("url");
        String customCode = body.get("customCode");
        if (originalUrl == null || originalUrl.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "URL is required"));
        }
        try {
            ShortUrl shortUrl = urlService.createPrivateShortUrl(email, originalUrl, customCode);
            return ResponseEntity.ok(Map.of(
                    "id", shortUrl.getId(),
                    "shortCode", shortUrl.getShortCode(),
                    "originalUrl", shortUrl.getOriginalUrl(),
                    "createdAt", shortUrl.getCreatedAt().toString(),
                    "expiredAt", shortUrl.getExpiredAt().toString(),
                    "clickCount", shortUrl.getClickCount()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get all URLs for authenticated user
    @GetMapping("/api/url/my-urls")
    public ResponseEntity<?> getMyUrls(HttpServletRequest request) {
        String email = extractEmail(request);
        List<ShortUrl> urls = urlService.getUrlsByUser(email);
        return ResponseEntity.ok(urls.stream().map(u -> Map.of(
                "id", u.getId(),
                "shortCode", u.getShortCode(),
                "originalUrl", u.getOriginalUrl(),
                "createdAt", u.getCreatedAt().toString(),
                "expiredAt", u.getExpiredAt().toString(),
                "clickCount", u.getClickCount()
        )).toList());
    }

    // Delete URL
    @DeleteMapping("/api/url/delete/{id}")
    public ResponseEntity<?> deleteShortUrl(@PathVariable Long id, HttpServletRequest request) {
        String email = extractEmail(request);
        try {
            urlService.deleteShortUrl(id, email);
            return ResponseEntity.ok(Map.of("message", "Short URL deleted successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Update expiration
    @PutMapping("/api/url/change-expired/{id}")
    public ResponseEntity<?> changeExpirationTime(@PathVariable Long id,
                                                   @RequestBody Map<String, String> body,
                                                   HttpServletRequest request) {
        String email = extractEmail(request);
        String newExpiration = body.get("expiredAt");
        if (newExpiration == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "expiredAt is required"));
        }
        try {
            ShortUrl updated = urlService.updateExpiration(id, email, LocalDateTime.parse(newExpiration));
            return ResponseEntity.ok(Map.of("message", "Expiration updated", "expiredAt", updated.getExpiredAt().toString()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get stats for authenticated user
    @GetMapping("/api/url/stats")
    public ResponseEntity<?> getStats(HttpServletRequest request) {
        String email = extractEmail(request);
        return ResponseEntity.ok(urlService.getUserStats(email));
    }

    // Expand short URL (redirect) - root level
    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode) {
        // Avoid matching API routes
        if (shortCode.startsWith("api") || shortCode.startsWith("actuator")) {
            return ResponseEntity.notFound().build();
        }
        try {
            String originalUrl = urlService.expandAndTrack(shortCode);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(originalUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private String extractEmail(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return jwtTokenProvider.getEmailFromToken(authHeader.substring(7));
    }
}
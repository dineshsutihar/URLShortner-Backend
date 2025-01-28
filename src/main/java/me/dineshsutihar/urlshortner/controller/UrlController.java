package me.dineshsutihar.urlshortner.controller;

import me.dineshsutihar.urlshortner.entity.ShortUrl;
import me.dineshsutihar.urlshortner.entity.User;
import me.dineshsutihar.urlshortner.repository.ShortUrlRepository;
import me.dineshsutihar.urlshortner.repository.UserRepository;
import me.dineshsutihar.urlshortner.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor
public class UrlController {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/public")
    public String createPublicShortUrl(@RequestParam String originalUrl) {
        String shortCode = generateShortCode();
        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(originalUrl)
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build();

        shortUrlRepository.save(shortUrl);
        return "Short URL created: /" + shortCode;
    }

    @PostMapping("/private")
    public String createPrivateShortUrl(HttpServletRequest request, @RequestParam String originalUrl, @RequestParam(required = false) String customShortCode) {
        User authenticatedUser = getAuthenticatedUser(request);
        String shortCode = (customShortCode != null) ? customShortCode : generateShortCode();

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(originalUrl)
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(30))
                .user(authenticatedUser)
                .build();

        shortUrlRepository.save(shortUrl);
        return "Private Short URL created: /" + shortCode;
    }


    @DeleteMapping("/delete/{id}")
    @Transactional
    public String deleteShortUrl(@PathVariable Long id) {
        Optional<ShortUrl> optionalShortUrl = shortUrlRepository.findById(id);

        if (optionalShortUrl.isPresent()) {
            shortUrlRepository.delete(optionalShortUrl.get());
            return "Short URL deleted successfully.";
        } else {
            return "Short URL not found.";
        }
    }

    @PutMapping("/change-expired/{id}")
    public String changeExpirationTime(@PathVariable Long id, @RequestParam String newExpiration) {
        Optional<ShortUrl> optionalShortUrl = shortUrlRepository.findById(id);

        if (optionalShortUrl.isPresent()) {
            ShortUrl shortUrl = optionalShortUrl.get();
            shortUrl.setExpiredAt(LocalDateTime.parse(newExpiration));
            shortUrlRepository.save(shortUrl);
            return "Expiration time updated.";
        } else {
            return "Short URL not found.";
        }
    }

    @GetMapping("/expand/{shortCode}")
    public String expandUrl(@PathVariable String shortCode) {
        Optional<ShortUrl> shortUrl = shortUrlRepository.findByShortCode(shortCode);

        if (shortUrl.isPresent() && shortUrl.get().getExpiredAt().isAfter(LocalDateTime.now())) {
            return "Redirect to: " + shortUrl.get().getOriginalUrl();
        } else {
            return "Short URL not found or expired.";
        }
    }

    // Helper: Generate a random short code
    private String generateShortCode() {
        return Long.toHexString(System.currentTimeMillis()).substring(0, 8); // Simple short code generator
    }

    // Helper: Get the authenticated user from the JWT
    private User getAuthenticatedUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtTokenProvider.getEmailFromToken(token);
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}

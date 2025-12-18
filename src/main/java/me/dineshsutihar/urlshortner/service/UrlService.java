package me.dineshsutihar.urlshortner.service;

import me.dineshsutihar.urlshortner.entity.ShortUrl;
import me.dineshsutihar.urlshortner.entity.User;
import me.dineshsutihar.urlshortner.repository.ShortUrlRepository;
import me.dineshsutihar.urlshortner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 7;
    private final SecureRandom secureRandom = new SecureRandom();

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;

    public String createPublicShortUrl(String originalUrl) {
        String shortCode = generateUniqueShortCode();
        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(originalUrl.trim())
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(30))
                .clickCount(0L)
                .build();
        shortUrlRepository.save(shortUrl);
        return shortCode;
    }

    public ShortUrl createPrivateShortUrl(String email, String originalUrl, String customShortCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String shortCode = (customShortCode != null && !customShortCode.isBlank())
                ? customShortCode : generateUniqueShortCode();

        if (customShortCode != null && shortUrlRepository.existsByShortCode(shortCode)) {
            throw new RuntimeException("Short code already in use. Please choose a different one.");
        }

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(originalUrl.trim())
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(30))
                .user(user)
                .clickCount(0L)
                .build();
        return shortUrlRepository.save(shortUrl);
    }

    @Transactional
    public String expandAndTrack(String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        if (shortUrl.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Short URL has expired");
        }

        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        shortUrlRepository.save(shortUrl);
        return shortUrl.getOriginalUrl();
    }

    public List<ShortUrl> getUrlsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return shortUrlRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public void deleteShortUrl(Long id, String email) {
        ShortUrl shortUrl = shortUrlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        if (shortUrl.getUser() == null || !shortUrl.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to delete this URL");
        }
        shortUrlRepository.delete(shortUrl);
    }

    @Transactional
    public ShortUrl updateExpiration(Long id, String email, LocalDateTime newExpiration) {
        ShortUrl shortUrl = shortUrlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        if (shortUrl.getUser() == null || !shortUrl.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to update this URL");
        }
        shortUrl.setExpiredAt(newExpiration);
        return shortUrlRepository.save(shortUrl);
    }

    public Map<String, Long> getUserStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        long totalLinks = shortUrlRepository.countByUser(user);
        Long totalClicks = shortUrlRepository.sumClickCountByUser(user);
        return Map.of(
                "totalLinks", totalLinks,
                "totalClicks", totalClicks != null ? totalClicks : 0L
        );
    }

    private String generateUniqueShortCode() {
        String code;
        int attempts = 0;
        do {
            code = generateShortCode();
            attempts++;
            if (attempts > 10) throw new RuntimeException("Unable to generate unique short code");
        } while (shortUrlRepository.existsByShortCode(code));
        return code;
    }

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}

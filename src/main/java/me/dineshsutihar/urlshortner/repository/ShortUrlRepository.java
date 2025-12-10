package me.dineshsutihar.urlshortner.repository;

import java.util.List;
import me.dineshsutihar.urlshortner.entity.ShortUrl;
import me.dineshsutihar.urlshortner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByShortCode(String shortCode);
    List<ShortUrl> findByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
    boolean existsByShortCode(String shortCode);

    @Query("SELECT SUM(s.clickCount) FROM ShortUrl s WHERE s.user = :user")
    Long sumClickCountByUser(User user);
}

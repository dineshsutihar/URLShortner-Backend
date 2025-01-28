package me.dineshsutihar.urlshortner.repository;

import java.util.List;
import me.dineshsutihar.urlshortner.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByShortCode(String shortCode);

}

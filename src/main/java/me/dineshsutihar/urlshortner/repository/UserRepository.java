package me.dineshsutihar.urlshortner.repository;

import java.util.Optional;
import me.dineshsutihar.urlshortner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
}

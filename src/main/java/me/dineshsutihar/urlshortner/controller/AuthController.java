package me.dineshsutihar.urlshortner.controller;

import me.dineshsutihar.urlshortner.entity.User;
import me.dineshsutihar.urlshortner.repository.UserRepository;
import me.dineshsutihar.urlshortner.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public String register(@RequestBody @Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully.";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());

        if (optionalUser.isPresent() && passwordEncoder.matches(user.getPassword(), optionalUser.get().getPassword())) {
            return jwtTokenProvider.createToken(user.getEmail());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}

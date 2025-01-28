package me.dineshsutihar.urlshortner.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.dineshsutihar.urlshortner.entity.User;
import me.dineshsutihar.urlshortner.repository.UserRepository;
import me.dineshsutihar.urlshortner.security.JwtTokenProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/profile")
    public User getUserProfile(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtTokenProvider.getEmailFromToken(token);

        return userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
    }

}

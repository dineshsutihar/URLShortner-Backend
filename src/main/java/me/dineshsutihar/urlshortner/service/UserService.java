package me.dineshsutihar.urlshortner.service;

import me.dineshsutihar.urlshortner.entity.User;
import me.dineshsutihar.urlshortner.repository.UserRepository;
import me.dineshsutihar.urlshortner.security.JwtTokenProvider;
import me.dineshsutihar.urlshortner.dto.LoginRequest;
import me.dineshsutihar.urlshortner.dto.RegisterRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;

    }

    public String register(RegisterRequest registerRequest){
        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw new RuntimeException("User already exists with this email.");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());

        userRepository.save(user);

        return jwtTokenProvider.createToken(user.getEmail());
    }

    public String login(LoginRequest loginRequest){
        Optional<user> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if(userOpt.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())){
            throw new RuntimeException("Invalid email or password.");
        }

        return jwtTokenProvider.createToken(userOpt.get().getEmail());
    }
}

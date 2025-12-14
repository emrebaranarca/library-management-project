package com.example.library_management.service;

import com.example.library_management.dto.auth.AuthResponse;
import com.example.library_management.dto.auth.LoginRequest;
import com.example.library_management.dto.auth.RegisterRequest;
import com.example.library_management.exception.BadRequestException;
import com.example.library_management.models.User;
import com.example.library_management.repository.UserRepository;
import com.example.library_management.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private AuthResponse buildAuthResponse(String token, User user){
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .build();
    }
    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("Email already exists");
        }

        User newUser= User.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.MEMBER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(newUser);

        log.info("New user registered: {}", newUser.getEmail());

        String token=jwtUtil.generateToken(newUser.getId(),newUser.getEmail(),newUser.getRole().name());

        return buildAuthResponse(token,newUser);
    }

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(), request.getPassword()
                )
        );
        User user=userRepository.findByEmail(request.getEmail().toLowerCase())
        .orElseThrow(() -> new BadRequestException("Email not found"));

        log.info("User {} logged in", user.getEmail());

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return buildAuthResponse(token,user);
    }
}


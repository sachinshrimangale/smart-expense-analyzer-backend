package com.smartexpenseanalyzer.service;

import com.smartexpenseanalyzer.dto.*;
import com.smartexpenseanalyzer.entity.User;
import com.smartexpenseanalyzer.repository.UserRepository;
import com.smartexpenseanalyzer.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .build();

        User savedUser = userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        savedUser.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase(),
                        request.getPassword()
                )
        );

        User user = userRepository
                .findByEmail(
                        request.getEmail().toLowerCase()
                )
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token =
                jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}


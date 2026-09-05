package com.smartexpenseanalyzer.service;

import com.smartexpenseanalyzer.dto.ChangePasswordRequest;
import com.smartexpenseanalyzer.dto.UpdateProfileRequest;
import com.smartexpenseanalyzer.dto.UserProfileResponse;
import com.smartexpenseanalyzer.entity.User;
import com.smartexpenseanalyzer.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponse getProfile(String email) {

        User user = getUser(email);

        return convertToResponse(user);
    }


    public UserProfileResponse updateProfile(
            String currentEmail,
            UpdateProfileRequest request) {

        User user = getUser(currentEmail);

        user.setName(request.getName().trim());

        User savedUser = userRepository.save(user);

        return convertToResponse(savedUser);
    }

    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        User user = getUser(email);

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "Current password is incorrect"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }

    private User getUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }

    private UserProfileResponse convertToResponse(User user) {

        UserProfileResponse response =
                new UserProfileResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return response;
    }
}
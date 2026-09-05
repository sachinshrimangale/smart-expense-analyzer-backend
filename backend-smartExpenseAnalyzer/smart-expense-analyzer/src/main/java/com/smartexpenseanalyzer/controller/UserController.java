package com.smartexpenseanalyzer.controller;

import com.smartexpenseanalyzer.dto.ChangePasswordRequest;
import com.smartexpenseanalyzer.dto.UpdateProfileRequest;
import com.smartexpenseanalyzer.dto.UserProfileResponse;
import com.smartexpenseanalyzer.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            Authentication authentication) {

        return ResponseEntity.ok(
                userService.getProfile(
                        authentication.getName()
                )
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                userService.updateProfile(
                        authentication.getName(),
                        request
                )
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        userService.changePassword(
                authentication.getName(),
                request
        );

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }
}
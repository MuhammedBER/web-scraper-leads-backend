package com.scraping.demo.controller;

import com.scraping.demo.dto.*;
import com.scraping.demo.service.UserService;
import com.scraping.demo.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user self-account management")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @PutMapping("/profile")
    @Operation(summary = "Update own profile", description = "Allows any authenticated user to update their own account information (name and email)")
    public ResponseEntity<UserDTO> updateProfile(@Valid @RequestBody UserSelfUpdateRequest request) {
        return ResponseEntity.ok(userService.updateOwnProfile(SecurityUtils.getCurrentUser(), request));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Allows a user to change their password by verifying the old one first")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(SecurityUtils.getCurrentUser(), request);
        return ResponseEntity.ok("Password changed successfully");
    }
}

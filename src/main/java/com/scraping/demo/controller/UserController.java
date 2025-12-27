package com.scraping.demo.controller;

import com.scraping.demo.dto.*;
import com.scraping.demo.service.UserService;
import com.scraping.demo.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user self-account management")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @PutMapping("/profile")
    @Operation(summary = "Update own profile", description = "Allows any authenticated user to partially update their own account information (name and email). Only provided fields will be updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already in use"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Valid JWT token required")
    })
    public ResponseEntity<UserDTO> updateProfile(@Valid @RequestBody UserSelfUpdateRequest request) {
        return ResponseEntity.ok(userService.updateOwnProfile(SecurityUtils.getCurrentUser(), request));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Allows a user to change their password by verifying the old one first. New password must be at least 8 characters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid old password or weak new password"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(SecurityUtils.getCurrentUser(), request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete own account", description = "Permanently removes the authenticated user's account and all associated data (files, extractions). This action cannot be undone.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteAccount() {
        userService.deleteAccount(SecurityUtils.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}

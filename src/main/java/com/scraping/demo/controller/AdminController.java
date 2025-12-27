package com.scraping.demo.controller;

import com.scraping.demo.dto.AdminCreateUserRequest;
import com.scraping.demo.dto.AdminUpdateUserRequest;
import com.scraping.demo.dto.UserDTO;
import com.scraping.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Endpoints for administrative user account management")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users (Admin only)")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Allows admin to manually create a new user account")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Allows admin to update any user's account information")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUserByAdmin(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Allows admin to permanently remove a user account")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

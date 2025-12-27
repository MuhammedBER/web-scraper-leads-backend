package com.scraping.demo.dto;

import com.scraping.demo.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request for an admin to create a new user")
public class AdminCreateUserRequest {

    @NotBlank(message = "First name is required")
    @Schema(description = "User's first name", example = "Jane")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "User's last name", example = "Smith")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    @Schema(description = "User's email address", example = "jane.smith@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "User's secret password", example = "strongPassword123")
    private String password;

    @Schema(description = "Role to assign to the user", example = "USER")
    private Role role;
}

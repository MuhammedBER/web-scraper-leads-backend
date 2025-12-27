package com.scraping.demo.dto;

import com.scraping.demo.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request for an admin to update a user's account")
public class AdminUpdateUserRequest {

    @NotBlank(message = "First name is required")
    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Role of the user", example = "ADMIN")
    private Role role;
}

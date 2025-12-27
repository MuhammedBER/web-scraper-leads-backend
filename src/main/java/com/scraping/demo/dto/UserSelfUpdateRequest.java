package com.scraping.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request for a user to update their own profile information")
public class UserSelfUpdateRequest {

    @Schema(description = "User's first name (optional)", example = "Johnny", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String firstName;

    @Schema(description = "User's last name (optional)", example = "Doe", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String lastName;

    @jakarta.validation.constraints.Email(message = "Valid email is required")
    @Schema(description = "User's email address (optional)", example = "johnny.doe@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;
}

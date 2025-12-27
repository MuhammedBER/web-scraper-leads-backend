package com.scraping.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request to verify a password reset code")
public class VerifyResetCodeRequest {

    @NotBlank(message = "Email is required")
    @Schema(description = "User's email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "Verification code is required")
    @Size(min = 6, max = 6, message = "Verification code must be 6 digits")
    @Schema(description = "6-digit verification code received by email", example = "123456")
    private String code;
}

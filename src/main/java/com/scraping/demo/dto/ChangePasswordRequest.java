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
@Schema(description = "Request for a user to change their password")
public class ChangePasswordRequest {

    @NotBlank(message = "Old password is required")
    @Schema(description = "The user's current password", example = "oldPassword123")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Schema(description = "The user's new desired password", example = "newSecurePassword456")
    private String newPassword;
}

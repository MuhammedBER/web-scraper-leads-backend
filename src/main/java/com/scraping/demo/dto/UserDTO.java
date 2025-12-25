package com.scraping.demo.dto;

import com.scraping.demo.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO representing a user")
public class UserDTO {

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's unique email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Role of the user in the system", example = "USER")
    private Role role;

    @Schema(description = "List of files owned by the user")
    private List<FileDTO> files;
}

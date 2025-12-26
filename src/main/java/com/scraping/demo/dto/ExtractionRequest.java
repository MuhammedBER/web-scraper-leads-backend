package com.scraping.demo.dto;

import com.scraping.demo.entity.SocialMediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request for starting a data extraction process")
public class ExtractionRequest {

    @NotNull(message = "File ID is required")
    @Schema(description = "ID of the file containing URLs to scrape", example = "1")
    private Long fileId;

    @Schema(description = "Whether to extract email addresses (Strict validation: domain match or whitelisted provider)", example = "true")
    private boolean extractEmails;

    @Schema(description = "Whether to extract phone numbers (Strict validation: tel: links or + prefix)", example = "true")
    private boolean extractPhones;

    @Schema(description = "List of social media platforms to target", example = "[\"FACEBOOK\", \"INSTAGRAM\"]")
    private List<SocialMediaType> socialMediaTypes;
}

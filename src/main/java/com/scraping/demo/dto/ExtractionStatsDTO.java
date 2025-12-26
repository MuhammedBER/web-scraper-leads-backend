package com.scraping.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO representing extraction statistics for a user")
public class ExtractionStatsDTO {

    @Schema(description = "Total number of extraction operations performed", example = "12")
    private int totalExtractions;

    @Schema(description = "Total number of emails extracted", example = "340")
    private long emailsCount;

    @Schema(description = "Total number of phone numbers extracted", example = "128")
    private long phonesCount;

    @Schema(description = "Total number of social media accounts extracted", example = "97")
    private long socialMediaCount;
}

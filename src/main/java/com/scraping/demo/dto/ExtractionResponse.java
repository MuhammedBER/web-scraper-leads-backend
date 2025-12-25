package com.scraping.demo.dto;

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
@Schema(description = "Response containing result of the extraction process")
public class ExtractionResponse {

    @Schema(description = "ID of the newly created file containing emails", example = "10")
    private Long emailFileId;

    @Schema(description = "ID of the newly created file containing phone numbers", example = "11")
    private Long phoneFileId;

    @Schema(description = "ID of the newly created file containing social media profiles", example = "12")
    private Long socialMediaFileId;

    @Schema(description = "Count of total emails extracted", example = "42")
    private int emailsExtracted;

    @Schema(description = "Count of total phone numbers extracted", example = "7")
    private int phonesExtracted;

    @Schema(description = "Count of total social media records extracted", example = "15")
    private int socialMediaExtracted;

    @Schema(description = "List of extracted email objects")
    private List<EmailDTO> emails;

    @Schema(description = "List of extracted phone number objects")
    private List<PhoneNumberDTO> phones;

    @Schema(description = "List of extracted social media profile objects")
    private List<SocialMediaDTO> socialMedia;

    @Schema(description = "Status message", example = "Extraction completed successfully")
    private String message;
}

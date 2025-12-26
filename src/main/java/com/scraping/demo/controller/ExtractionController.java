package com.scraping.demo.controller;

import com.scraping.demo.dto.*;
import com.scraping.demo.service.ExtractionService;
import com.scraping.demo.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/extraction")
@RequiredArgsConstructor
@Tag(name = "Extraction", description = "Web scraping extraction endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class ExtractionController {

    private final ExtractionService extractionService;

    @PostMapping("/extract")
    @Operation(summary = "Extract data from URLs", description = "Extracts emails, phone numbers, and social media from URLs in a file")
    public ResponseEntity<ExtractionResponse> extractData(@Valid @RequestBody ExtractionRequest request) {
        ExtractionResponse response = extractionService.extractData(request, SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/files/{fileId}/emails")
    @Operation(summary = "Get extracted emails", description = "Retrieves all emails extracted for a file")
    public ResponseEntity<List<EmailDTO>> getExtractedEmails(@PathVariable Long fileId) {
        List<EmailDTO> emails = extractionService.getExtractedEmails(fileId, SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/files/{fileId}/phones")
    @Operation(summary = "Get extracted phone numbers", description = "Retrieves all phone numbers extracted for a file")
    public ResponseEntity<List<PhoneNumberDTO>> getExtractedPhones(@PathVariable Long fileId) {
        List<PhoneNumberDTO> phones = extractionService.getExtractedPhones(fileId, SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(phones);
    }

    @GetMapping("/files/{fileId}/social-media")
    @Operation(summary = "Get extracted social media", description = "Retrieves all social media profiles extracted for a file")
    public ResponseEntity<List<SocialMediaDTO>> getExtractedSocialMedia(@PathVariable Long fileId) {
        List<SocialMediaDTO> socialMedia = extractionService.getExtractedSocialMedia(fileId,
                SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(socialMedia);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get extraction statistics", description = "Returns a summary of all extraction activity for the authenticated user")
    public ResponseEntity<ExtractionStatsDTO> getStats() {
        ExtractionStatsDTO stats = extractionService.getStats(SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(stats);
    }
}

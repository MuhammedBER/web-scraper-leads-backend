package com.scraping.demo.controller;

import com.scraping.demo.dto.AddUrlsRequest;
import com.scraping.demo.dto.UrlDTO;
import com.scraping.demo.service.UrlService;
import com.scraping.demo.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "URLs", description = "URL management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/files/{fileId}/urls")
    @Operation(summary = "Get URLs for file", description = "Retrieves all URLs associated with a specific file")
    public ResponseEntity<List<UrlDTO>> getUrlsByFileId(@PathVariable Long fileId) {
        List<UrlDTO> urls = urlService.getUrlsByFileId(fileId, SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(urls);
    }

    @PostMapping("/files/{fileId}/urls")
    @Operation(summary = "Add URLs to file", description = "Adds new URLs to an existing file")
    public ResponseEntity<List<UrlDTO>> addUrlsToFile(
            @PathVariable Long fileId,
            @Valid @RequestBody AddUrlsRequest request) {
        List<UrlDTO> addedUrls = urlService.addUrlsToFile(fileId, request, SecurityUtils.getCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(addedUrls);
    }

    @DeleteMapping("/urls/{id}")
    @Operation(summary = "Delete URL", description = "Deletes a specific URL")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id) {
        urlService.deleteUrl(id, SecurityUtils.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}

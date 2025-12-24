package com.scraping.demo.controller;

import com.scraping.demo.dto.CreateFileRequest;
import com.scraping.demo.dto.FileDTO;
import com.scraping.demo.dto.UpdateFileRequest;
import com.scraping.demo.service.FileService;
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
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "File management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class FileController {

    private final FileService fileService;

    @PostMapping
    @Operation(summary = "Create a new file with URLs", description = "Creates a new file and optionally adds URLs to it")
    public ResponseEntity<FileDTO> createFile(@Valid @RequestBody CreateFileRequest request) {
        FileDTO createdFile = fileService.createFile(request, SecurityUtils.getCurrentUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFile);
    }

    @GetMapping
    @Operation(summary = "Get all files", description = "Retrieves all files for the authenticated user")
    public ResponseEntity<List<FileDTO>> getAllFiles() {
        List<FileDTO> files = fileService.getAllFiles(SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get file by ID", description = "Retrieves a specific file by its ID")
    public ResponseEntity<FileDTO> getFileById(@PathVariable Long id) {
        FileDTO file = fileService.getFileById(id, SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(file);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update file", description = "Updates file name and/or type")
    public ResponseEntity<FileDTO> updateFile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFileRequest request) {
        FileDTO updatedFile = fileService.updateFile(id, request, SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(updatedFile);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete file", description = "Deletes a file and all associated URLs")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id, SecurityUtils.getCurrentUser());
        return ResponseEntity.noContent().build();
    }
}

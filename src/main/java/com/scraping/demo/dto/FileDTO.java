package com.scraping.demo.dto;

import com.scraping.demo.entity.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO representing a file and its relationships")
public class FileDTO {

    @Schema(description = "Unique ID of the file", example = "1")
    private Long id;

    @Schema(description = "Name of the file", example = "company_data")
    private String name;

    @Schema(description = "Type of the file", example = "URL")
    private FileType type;

    @Schema(description = "ID of the parent file if this is an extracted result", example = "1")
    private Long parentFileId;

    @Schema(description = "List of URLs associated with this file")
    private List<UrlDTO> urls;

    @Schema(description = "List of child files (extracted results)")
    private List<FileDTO> childFiles;

    @Schema(description = "Timestamp when the file was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the file was last updated")
    private LocalDateTime updatedAt;
}

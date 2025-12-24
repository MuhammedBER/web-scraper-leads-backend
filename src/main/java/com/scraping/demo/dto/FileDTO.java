package com.scraping.demo.dto;

import com.scraping.demo.entity.FileType;
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
public class FileDTO {
    private Long id;
    private String name;
    private FileType type;
    private List<UrlDTO> urls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

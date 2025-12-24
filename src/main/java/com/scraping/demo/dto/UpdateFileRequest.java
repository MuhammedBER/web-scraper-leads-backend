package com.scraping.demo.dto;

import com.scraping.demo.entity.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFileRequest {
    private String name;
    private FileType type;
}

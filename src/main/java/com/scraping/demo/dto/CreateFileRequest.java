package com.scraping.demo.dto;

import com.scraping.demo.entity.FileType;
import jakarta.validation.constraints.NotBlank;
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
public class CreateFileRequest {

    @NotBlank(message = "File name is required")
    private String name;

    @NotNull(message = "File type is required")
    private FileType type;

    private List<String> urls;
}

package com.scraping.demo.dto;

import com.scraping.demo.entity.SocialMediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialMediaDTO {
    private Long id;
    private String content;
    private SocialMediaType type;
}

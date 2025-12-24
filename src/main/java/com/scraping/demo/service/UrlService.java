package com.scraping.demo.service;

import com.scraping.demo.dto.AddUrlsRequest;
import com.scraping.demo.dto.UrlDTO;
import com.scraping.demo.entity.FileEntity;
import com.scraping.demo.entity.UrlEntity;
import com.scraping.demo.entity.User;
import com.scraping.demo.repository.FileRepository;
import com.scraping.demo.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final FileRepository fileRepository;

    public List<UrlDTO> getUrlsByFileId(Long fileId, User user) {
        // Verify user owns the file
        FileEntity file = fileRepository.findByIdAndUserId(fileId, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));

        return file.getUrls().stream()
                .map(url -> UrlDTO.builder()
                        .id(url.getId())
                        .content(url.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UrlDTO> addUrlsToFile(Long fileId, AddUrlsRequest request, User user) {
        FileEntity file = fileRepository.findByIdAndUserId(fileId, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));

        List<UrlEntity> newUrls = request.getUrls().stream()
                .map(urlContent -> UrlEntity.builder()
                        .content(urlContent)
                        .file(file)
                        .build())
                .collect(Collectors.toList());

        List<UrlEntity> savedUrls = urlRepository.saveAll(newUrls);

        return savedUrls.stream()
                .map(url -> UrlDTO.builder()
                        .id(url.getId())
                        .content(url.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUrl(Long urlId, User user) {
        UrlEntity url = urlRepository.findById(urlId)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        // Verify user owns the file that contains this URL
        FileEntity file = url.getFile();
        if (!file.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        urlRepository.delete(url);
    }
}

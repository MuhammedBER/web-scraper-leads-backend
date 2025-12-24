package com.scraping.demo.service;

import com.scraping.demo.dto.*;
import com.scraping.demo.entity.FileEntity;
import com.scraping.demo.entity.UrlEntity;
import com.scraping.demo.entity.User;
import com.scraping.demo.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Transactional
    public FileDTO createFile(CreateFileRequest request, User user) {
        FileEntity fileEntity = FileEntity.builder()
                .name(request.getName())
                .type(request.getType())
                .user(user)
                .build();

        // Add URLs if provided
        if (request.getUrls() != null && !request.getUrls().isEmpty()) {
            List<UrlEntity> urlEntities = request.getUrls().stream()
                    .map(url -> UrlEntity.builder()
                            .content(url)
                            .file(fileEntity)
                            .build())
                    .collect(Collectors.toList());
            fileEntity.getUrls().addAll(urlEntities);
        }

        FileEntity savedFile = fileRepository.save(fileEntity);
        return mapToDTO(savedFile);
    }

    public List<FileDTO> getAllFiles(User user) {
        return fileRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public FileDTO getFileById(Long id, User user) {
        FileEntity file = fileRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));
        return mapToDTO(file);
    }

    @Transactional
    public FileDTO updateFile(Long id, UpdateFileRequest request, User user) {
        FileEntity file = fileRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));

        if (request.getName() != null) {
            file.setName(request.getName());
        }
        if (request.getType() != null) {
            file.setType(request.getType());
        }

        FileEntity updatedFile = fileRepository.save(file);
        return mapToDTO(updatedFile);
    }

    @Transactional
    public void deleteFile(Long id, User user) {
        FileEntity file = fileRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));
        fileRepository.delete(file);
    }

    private FileDTO mapToDTO(FileEntity file) {
        List<UrlDTO> urlDTOs = file.getUrls().stream()
                .map(url -> UrlDTO.builder()
                        .id(url.getId())
                        .content(url.getContent())
                        .build())
                .collect(Collectors.toList());

        return FileDTO.builder()
                .id(file.getId())
                .name(file.getName())
                .type(file.getType())
                .urls(urlDTOs)
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}

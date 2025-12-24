package com.scraping.demo.repository;

import com.scraping.demo.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByUserId(Long userId);

    Optional<FileEntity> findByIdAndUserId(Long id, Long userId);
}

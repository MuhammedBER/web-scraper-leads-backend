package com.scraping.demo.repository;

import com.scraping.demo.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<EmailEntity, Long> {
    List<EmailEntity> findByFileId(Long fileId);

    boolean existsByContentAndFileId(String content, Long fileId);
}

package com.scraping.demo.repository;

import com.scraping.demo.entity.SocialMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocialMediaRepository extends JpaRepository<SocialMediaEntity, Long> {
    List<SocialMediaEntity> findByFileId(Long fileId);

    boolean existsByContentAndFileId(String content, Long fileId);
}

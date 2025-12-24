package com.scraping.demo.repository;

import com.scraping.demo.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    List<UrlEntity> findByFileId(Long fileId);

    void deleteByFileId(Long fileId);
}

package com.scraping.demo.repository;

import com.scraping.demo.entity.SocialMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SocialMediaRepository extends JpaRepository<SocialMediaEntity, Long> {
    List<SocialMediaEntity> findByFileId(Long fileId);

    boolean existsByContentAndFileId(String content, Long fileId);

    void deleteByFileId(Long fileId);

    long countByFileUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SocialMediaEntity s WHERE s.file.parentFile.id = :sourceFileId")
    void deleteBySourceFileId(Long sourceFileId);
}

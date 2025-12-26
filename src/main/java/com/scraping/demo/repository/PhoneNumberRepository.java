package com.scraping.demo.repository;

import com.scraping.demo.entity.PhoneNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumberEntity, Long> {
    List<PhoneNumberEntity> findByFileId(Long fileId);

    boolean existsByContentAndFileId(String content, Long fileId);

    void deleteByFileId(Long fileId);

    long countByFileUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PhoneNumberEntity p WHERE p.file.parentFile.id = :sourceFileId")
    void deleteBySourceFileId(Long sourceFileId);
}

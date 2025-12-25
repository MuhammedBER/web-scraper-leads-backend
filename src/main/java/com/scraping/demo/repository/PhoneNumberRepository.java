package com.scraping.demo.repository;

import com.scraping.demo.entity.PhoneNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumberEntity, Long> {
    List<PhoneNumberEntity> findByFileId(Long fileId);

    boolean existsByContentAndFileId(String content, Long fileId);
}

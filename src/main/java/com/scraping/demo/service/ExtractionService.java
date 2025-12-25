package com.scraping.demo.service;

import com.scraping.demo.dto.*;
import com.scraping.demo.entity.*;
import com.scraping.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExtractionService {

    private final FileRepository fileRepository;
    private final EmailRepository emailRepository;
    private final PhoneNumberRepository phoneRepository;
    private final SocialMediaRepository socialMediaRepository;
    private final WebScraperService webScraperService;

    @Transactional
    public ExtractionResponse extractData(ExtractionRequest request, User user) {
        // Verify user owns the file
        FileEntity sourceFile = fileRepository.findByIdAndUserId(request.getFileId(), user.getId())
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));

        log.info("Starting extraction for file: {} (ID: {})", sourceFile.getName(), sourceFile.getId());

        // Create files for extracted data
        FileEntity emailFile = null;
        FileEntity phoneFile = null;
        FileEntity socialMediaFile = null;

        List<EmailDTO> extractedEmails = new ArrayList<>();
        List<PhoneNumberDTO> extractedPhones = new ArrayList<>();
        List<SocialMediaDTO> extractedSocialMedia = new ArrayList<>();

        // Extract from each URL
        for (UrlEntity urlEntity : sourceFile.getUrls()) {
            String content = webScraperService.fetchPageContent(urlEntity.getContent());

            if (content.isEmpty()) {
                log.warn("Failed to fetch content from: {}", urlEntity.getContent());
                continue;
            }

            // Extract emails
            if (request.isExtractEmails()) {
                if (emailFile == null) {
                    emailFile = createExtractedFile(sourceFile, FileType.EMAIL, user);
                }
                Set<String> emails = webScraperService.extractEmails(content);
                extractedEmails.addAll(saveEmails(emails, emailFile));
            }

            // Extract phone numbers
            if (request.isExtractPhones()) {
                if (phoneFile == null) {
                    phoneFile = createExtractedFile(sourceFile, FileType.PHONE, user);
                }
                Set<String> phones = webScraperService.extractPhoneNumbers(content);
                extractedPhones.addAll(savePhones(phones, phoneFile));
            }

            // Extract social media
            if (request.getSocialMediaTypes() != null && !request.getSocialMediaTypes().isEmpty()) {
                if (socialMediaFile == null) {
                    socialMediaFile = createExtractedFile(sourceFile, FileType.SOCIAL_MEDIA, user);
                }
                Set<WebScraperService.SocialMediaMatch> socialMedia = webScraperService.extractSocialMedia(content,
                        request.getSocialMediaTypes());
                extractedSocialMedia.addAll(saveSocialMedia(socialMedia, socialMediaFile));
            }
        }

        // Build response
        return ExtractionResponse.builder()
                .emailFileId(emailFile != null ? emailFile.getId() : null)
                .phoneFileId(phoneFile != null ? phoneFile.getId() : null)
                .socialMediaFileId(socialMediaFile != null ? socialMediaFile.getId() : null)
                .emailsExtracted(extractedEmails.size())
                .phonesExtracted(extractedPhones.size())
                .socialMediaExtracted(extractedSocialMedia.size())
                .emails(extractedEmails)
                .phones(extractedPhones)
                .socialMedia(extractedSocialMedia)
                .message("Extraction completed successfully")
                .build();
    }

    private FileEntity createExtractedFile(FileEntity sourceFile, FileType type, User user) {
        String typeSuffix = switch (type) {
            case EMAIL -> "_emails";
            case PHONE -> "_phones";
            case SOCIAL_MEDIA -> "_social_media";
            default -> "_extracted";
        };

        FileEntity extractedFile = FileEntity.builder()
                .name(sourceFile.getName() + typeSuffix)
                .type(type)
                .user(user)
                .build();

        return fileRepository.save(extractedFile);
    }

    private List<EmailDTO> saveEmails(Set<String> emails, FileEntity file) {
        List<EmailDTO> saved = new ArrayList<>();

        for (String email : emails) {
            // Check for duplicates
            if (!emailRepository.existsByContentAndFileId(email, file.getId())) {
                EmailEntity entity = EmailEntity.builder()
                        .content(email)
                        .file(file)
                        .build();
                EmailEntity savedEntity = emailRepository.save(entity);
                saved.add(EmailDTO.builder()
                        .id(savedEntity.getId())
                        .content(savedEntity.getContent())
                        .build());
            }
        }

        return saved;
    }

    private List<PhoneNumberDTO> savePhones(Set<String> phones, FileEntity file) {
        List<PhoneNumberDTO> saved = new ArrayList<>();

        for (String phone : phones) {
            if (!phoneRepository.existsByContentAndFileId(phone, file.getId())) {
                PhoneNumberEntity entity = PhoneNumberEntity.builder()
                        .content(phone)
                        .file(file)
                        .build();
                PhoneNumberEntity savedEntity = phoneRepository.save(entity);
                saved.add(PhoneNumberDTO.builder()
                        .id(savedEntity.getId())
                        .content(savedEntity.getContent())
                        .build());
            }
        }

        return saved;
    }

    private List<SocialMediaDTO> saveSocialMedia(Set<WebScraperService.SocialMediaMatch> socialMedia, FileEntity file) {
        List<SocialMediaDTO> saved = new ArrayList<>();

        for (WebScraperService.SocialMediaMatch match : socialMedia) {
            if (!socialMediaRepository.existsByContentAndFileId(match.url, file.getId())) {
                SocialMediaEntity entity = SocialMediaEntity.builder()
                        .content(match.url)
                        .type(match.type)
                        .file(file)
                        .build();
                SocialMediaEntity savedEntity = socialMediaRepository.save(entity);
                saved.add(SocialMediaDTO.builder()
                        .id(savedEntity.getId())
                        .content(savedEntity.getContent())
                        .type(savedEntity.getType())
                        .build());
            }
        }

        return saved;
    }

    // Get extracted data methods
    public List<EmailDTO> getExtractedEmails(Long fileId, User user) {
        FileEntity file = fileRepository.findByIdAndUserId(fileId, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));

        return emailRepository.findByFileId(file.getId()).stream()
                .map(e -> EmailDTO.builder().id(e.getId()).content(e.getContent()).build())
                .collect(Collectors.toList());
    }

    public List<PhoneNumberDTO> getExtractedPhones(Long fileId, User user) {
        FileEntity file = fileRepository.findByIdAndUserId(fileId, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));

        return phoneRepository.findByFileId(file.getId()).stream()
                .map(p -> PhoneNumberDTO.builder().id(p.getId()).content(p.getContent()).build())
                .collect(Collectors.toList());
    }

    public List<SocialMediaDTO> getExtractedSocialMedia(Long fileId, User user) {
        FileEntity file = fileRepository.findByIdAndUserId(fileId, user.getId())
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));

        return socialMediaRepository.findByFileId(file.getId()).stream()
                .map(s -> SocialMediaDTO.builder()
                        .id(s.getId())
                        .content(s.getContent())
                        .type(s.getType())
                        .build())
                .collect(Collectors.toList());
    }
}

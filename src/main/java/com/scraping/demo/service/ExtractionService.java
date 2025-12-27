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
    private final UserRepository userRepository;
    private final EmailService emailService;
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
                    emailFile = getOrCreateExtractedFile(sourceFile, FileType.EMAIL, user);
                    emailRepository.deleteByFileId(emailFile.getId());
                }
                Set<String> emails = webScraperService.extractEmails(content, urlEntity.getContent());
                extractedEmails.addAll(saveEmails(emails, emailFile));
            }

            // Extract phone numbers
            if (request.isExtractPhones()) {
                if (phoneFile == null) {
                    phoneFile = getOrCreateExtractedFile(sourceFile, FileType.PHONE, user);
                    phoneRepository.deleteByFileId(phoneFile.getId());
                }
                Set<String> phones = webScraperService.extractPhoneNumbers(content);
                extractedPhones.addAll(savePhones(phones, phoneFile));
            }

            // Extract social media
            if (request.getSocialMediaTypes() != null && !request.getSocialMediaTypes().isEmpty()) {
                if (socialMediaFile == null) {
                    socialMediaFile = getOrCreateExtractedFile(sourceFile, FileType.SOCIAL_MEDIA, user);
                }

                // For each requested type, delete existing ones once
                for (SocialMediaType type : request.getSocialMediaTypes()) {
                    // We need to ensure we only delete once per extraction session per type
                    // But wait, it's easier to just delete them BEFORE the loop over URLs
                }

                Set<WebScraperService.SocialMediaMatch> socialMedia = webScraperService.extractSocialMedia(content,
                        request.getSocialMediaTypes());
                extractedSocialMedia.addAll(saveSocialMedia(socialMedia, socialMediaFile));
            }
        }

        // Increment user's total extractions count
        user.setTotalExtractions(user.getTotalExtractions() + 1);
        userRepository.save(user);

        // Build response
        ExtractionResponse response = ExtractionResponse.builder()
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

        // Send completion email
        emailService.sendExtractionCompletionEmail(user.getEmail(), sourceFile.getName());

        return response;
    }

    private FileEntity getOrCreateExtractedFile(FileEntity sourceFile, FileType type, User user) {
        String name = sourceFile.getName() + switch (type) {
            case EMAIL -> "_emails";
            case PHONE -> "_phones";
            case SOCIAL_MEDIA -> "_social_media";
            default -> "_extracted";
        };

        return fileRepository.findByNameAndParentFileId(name, sourceFile.getId())
                .orElseGet(() -> {
                    FileEntity newFile = FileEntity.builder()
                            .name(name)
                            .type(type)
                            .user(user)
                            .parentFile(sourceFile)
                            .build();
                    return fileRepository.save(newFile);
                });
    }

    @Transactional
    public ExtractionResponse extractDataSelective(ExtractionRequest request, User user) {
        // Redefined to use selective replacement
        FileEntity sourceFile = fileRepository.findByIdAndUserId(request.getFileId(), user.getId())
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));

        log.info("Starting selective extraction for file: {} (ID: {})", sourceFile.getName(), sourceFile.getId());

        FileEntity emailFile = request.isExtractEmails() ? getOrCreateExtractedFile(sourceFile, FileType.EMAIL, user)
                : null;
        FileEntity phoneFile = request.isExtractPhones() ? getOrCreateExtractedFile(sourceFile, FileType.PHONE, user)
                : null;
        FileEntity socialMediaFile = (request.getSocialMediaTypes() != null && !request.getSocialMediaTypes().isEmpty())
                ? getOrCreateExtractedFile(sourceFile, FileType.SOCIAL_MEDIA, user)
                : null;

        // Selective Deletion
        if (emailFile != null)
            emailRepository.deleteByFileId(emailFile.getId());
        if (phoneFile != null)
            phoneRepository.deleteByFileId(phoneFile.getId());
        if (socialMediaFile != null) {
            for (SocialMediaType type : request.getSocialMediaTypes()) {
                socialMediaRepository.deleteByFileIdAndType(socialMediaFile.getId(), type);
            }
        }

        List<EmailDTO> extractedEmails = new ArrayList<>();
        List<PhoneNumberDTO> extractedPhones = new ArrayList<>();
        List<SocialMediaDTO> extractedSocialMedia = new ArrayList<>();

        for (UrlEntity urlEntity : sourceFile.getUrls()) {
            String content = webScraperService.fetchPageContent(urlEntity.getContent());
            if (content.isEmpty())
                continue;

            if (emailFile != null) {
                Set<String> emails = webScraperService.extractEmails(content, urlEntity.getContent());
                extractedEmails.addAll(saveEmails(emails, emailFile));
            }

            if (phoneFile != null) {
                Set<String> phones = webScraperService.extractPhoneNumbers(content);
                extractedPhones.addAll(savePhones(phones, phoneFile));
            }

            if (socialMediaFile != null) {
                Set<WebScraperService.SocialMediaMatch> socialMedia = webScraperService.extractSocialMedia(content,
                        request.getSocialMediaTypes());
                extractedSocialMedia.addAll(saveSocialMedia(socialMedia, socialMediaFile));
            }
        }

        user.setTotalExtractions(user.getTotalExtractions() + 1);
        userRepository.save(user);

        // Deduplicate response lists (they might have been added multiple times if same
        // data found on different URLs)
        extractedEmails = extractedEmails.stream().distinct().collect(Collectors.toList());
        extractedPhones = extractedPhones.stream().distinct().collect(Collectors.toList());
        extractedSocialMedia = extractedSocialMedia.stream().distinct().collect(Collectors.toList());

        ExtractionResponse response = ExtractionResponse.builder()
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

        emailService.sendExtractionCompletionEmail(user.getEmail(), sourceFile.getName());
        return response;
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

    public ExtractionStatsDTO getStats(User user) {
        return ExtractionStatsDTO.builder()
                .totalExtractions(user.getTotalExtractions())
                .emailsCount(emailRepository.countByFileUserId(user.getId()))
                .phonesCount(phoneRepository.countByFileUserId(user.getId()))
                .socialMediaCount(socialMediaRepository.countByFileUserId(user.getId()))
                .build();
    }
}

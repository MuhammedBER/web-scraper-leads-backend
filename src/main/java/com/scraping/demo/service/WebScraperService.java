package com.scraping.demo.service;

import com.scraping.demo.entity.SocialMediaType;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class WebScraperService {

    private static final int TIMEOUT_MS = 10000; // 10 seconds timeout

    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "\\+?[0-9]{1,4}?[-.\\s]?\\(?[0-9]{1,3}?\\)?[-.\\s]?[0-9]{1,4}[-.\\s]?[0-9]{1,4}[-.\\s]?[0-9]{1,9}");

    /**
     * Fetch page content using Jsoup
     */
    public String fetchPageContent(String url) {
        try {
            log.info("Fetching content from URL: {}", url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(TIMEOUT_MS)
                    .get();
            return doc.html();
        } catch (IOException e) {
            log.error("Failed to fetch content from URL: {}", url, e);
            return "";
        }
    }

    /**
     * Extract emails using Jsoup and regex fallback
     */
    public Set<String> extractEmails(String content) {
        Set<String> emails = new HashSet<>();

        try {
            // Try Jsoup first - look for mailto links
            Document doc = Jsoup.parse(content);
            Elements mailtoLinks = doc.select("a[href^=mailto:]");
            for (Element link : mailtoLinks) {
                String email = link.attr("href").replace("mailto:", "").trim();
                if (isValidEmail(email)) {
                    emails.add(email.toLowerCase());
                }
            }

            // Fallback to regex for text content
            Matcher matcher = EMAIL_PATTERN.matcher(content);
            while (matcher.find()) {
                String email = matcher.group().trim();
                if (isValidEmail(email)) {
                    emails.add(email.toLowerCase());
                }
            }

            log.info("Extracted {} unique emails", emails.size());
        } catch (Exception e) {
            log.error("Error extracting emails", e);
        }

        return emails;
    }

    /**
     * Extract phone numbers using regex
     */
    public Set<String> extractPhoneNumbers(String content) {
        Set<String> phones = new HashSet<>();

        try {
            Matcher matcher = PHONE_PATTERN.matcher(content);
            while (matcher.find()) {
                String phone = matcher.group().trim();
                // Basic validation - at least 7 digits
                if (phone.replaceAll("[^0-9]", "").length() >= 7) {
                    phones.add(phone);
                }
            }

            log.info("Extracted {} unique phone numbers", phones.size());
        } catch (Exception e) {
            log.error("Error extracting phone numbers", e);
        }

        return phones;
    }

    /**
     * Extract social media profiles
     */
    public Set<SocialMediaMatch> extractSocialMedia(String content, List<SocialMediaType> types) {
        Set<SocialMediaMatch> socialMedia = new HashSet<>();

        try {
            Document doc = Jsoup.parse(content);

            for (SocialMediaType type : types) {
                switch (type) {
                    case FACEBOOK:
                        socialMedia.addAll(extractFacebook(doc, content));
                        break;
                    case INSTAGRAM:
                        socialMedia.addAll(extractInstagram(doc, content));
                        break;
                    case LINKEDIN:
                        socialMedia.addAll(extractLinkedIn(doc, content));
                        break;
                    case TWITTER:
                        socialMedia.addAll(extractTwitter(doc, content));
                        break;
                }
            }

            log.info("Extracted {} unique social media profiles", socialMedia.size());
        } catch (Exception e) {
            log.error("Error extracting social media", e);
        }

        return socialMedia;
    }

    private Set<SocialMediaMatch> extractFacebook(Document doc, String content) {
        Set<SocialMediaMatch> results = new HashSet<>();

        // Jsoup - look for Facebook links
        Elements links = doc.select("a[href*=facebook.com]");
        for (Element link : links) {
            String url = link.attr("abs:href");
            if (isValidFacebookUrl(url)) {
                results.add(new SocialMediaMatch(url, SocialMediaType.FACEBOOK));
            }
        }

        // Regex fallback
        Pattern pattern = Pattern.compile("https?://(?:www\\.)?facebook\\.com/[a-zA-Z0-9.]+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String url = matcher.group();
            if (isValidFacebookUrl(url)) {
                results.add(new SocialMediaMatch(url, SocialMediaType.FACEBOOK));
            }
        }

        return results;
    }

    private Set<SocialMediaMatch> extractInstagram(Document doc, String content) {
        Set<SocialMediaMatch> results = new HashSet<>();

        Elements links = doc.select("a[href*=instagram.com]");
        for (Element link : links) {
            String url = link.attr("abs:href");
            if (isValidInstagramUrl(url)) {
                results.add(new SocialMediaMatch(url, SocialMediaType.INSTAGRAM));
            }
        }

        Pattern pattern = Pattern.compile("https?://(?:www\\.)?instagram\\.com/[a-zA-Z0-9._]+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String url = matcher.group();
            if (isValidInstagramUrl(url)) {
                results.add(new SocialMediaMatch(url, SocialMediaType.INSTAGRAM));
            }
        }

        return results;
    }

    private Set<SocialMediaMatch> extractLinkedIn(Document doc, String content) {
        Set<SocialMediaMatch> results = new HashSet<>();

        Elements links = doc.select("a[href*=linkedin.com]");
        for (Element link : links) {
            String url = link.attr("abs:href");
            if (isValidLinkedInUrl(url)) {
                results.add(new SocialMediaMatch(url, SocialMediaType.LINKEDIN));
            }
        }

        Pattern pattern = Pattern.compile("https?://(?:www\\.)?linkedin\\.com/(?:in|company)/[a-zA-Z0-9-]+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String url = matcher.group();
            if (isValidLinkedInUrl(url)) {
                results.add(new SocialMediaMatch(url, SocialMediaType.LINKEDIN));
            }
        }

        return results;
    }

    private Set<SocialMediaMatch> extractTwitter(Document doc, String content) {
        Set<SocialMediaMatch> results = new HashSet<>();

        Elements links = doc.select("a[href*=twitter.com], a[href*=x.com]");
        for (Element link : links) {
            String url = link.attr("abs:href");
            if (isValidTwitterUrl(url)) {
                results.add(new SocialMediaMatch(url, SocialMediaType.TWITTER));
            }
        }

        Pattern pattern = Pattern.compile("https?://(?:www\\.)?(twitter|x)\\.com/[a-zA-Z0-9_]+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String url = matcher.group();
            if (isValidTwitterUrl(url)) {
                results.add(new SocialMediaMatch(url, SocialMediaType.TWITTER));
            }
        }

        return results;
    }

    // Validation methods
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".") && email.length() > 5;
    }

    private boolean isValidFacebookUrl(String url) {
        return url != null && url.contains("facebook.com/") && !url.contains("/sharer");
    }

    private boolean isValidInstagramUrl(String url) {
        return url != null && url.contains("instagram.com/") && !url.endsWith("instagram.com/");
    }

    private boolean isValidLinkedInUrl(String url) {
        return url != null && url.contains("linkedin.com/") &&
                (url.contains("/in/") || url.contains("/company/"));
    }

    private boolean isValidTwitterUrl(String url) {
        return url != null && (url.contains("twitter.com/") || url.contains("x.com/")) &&
                !url.contains("/status/") && !url.contains("/search");
    }

    // Inner class for social media matches
    public static class SocialMediaMatch {
        public final String url;
        public final SocialMediaType type;

        public SocialMediaMatch(String url, SocialMediaType type) {
            this.url = url;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            SocialMediaMatch that = (SocialMediaMatch) o;
            return url.equals(that.url) && type == that.type;
        }

        @Override
        public int hashCode() {
            return url.hashCode() + type.hashCode();
        }
    }
}

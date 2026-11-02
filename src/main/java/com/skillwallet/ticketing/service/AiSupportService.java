package com.skillwallet.ticketing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillwallet.ticketing.dto.AiSuggestionDto;
import com.skillwallet.ticketing.model.Category;
import com.skillwallet.ticketing.model.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiSupportService {

    private static final Logger log = LoggerFactory.getLogger(AiSupportService.class);

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiSuggestionDto analyzeAndDraft(String subject, String description, String customerName) {
        String cleanSubject = subject == null ? "" : subject.trim();
        String cleanDesc = description == null ? "" : description.trim();
        String cleanName = customerName == null || customerName.trim().isEmpty() ? "Valued Customer" : customerName.trim();

        // 1. Try Gemini Live API if API Key is configured
        if (geminiApiKey != null && !geminiApiKey.trim().isEmpty() && !geminiApiKey.contains("your_api_key")) {
            try {
                AiSuggestionDto geminiResult = callGeminiApi(cleanSubject, cleanDesc, cleanName);
                if (geminiResult != null) {
                    return geminiResult;
                }
            } catch (Exception ex) {
                log.warn("Gemini API call failed or timed out: {}. Seamlessly falling back to Rule-Based Demo Mode.", ex.getMessage());
            }
        }

        // 2. High-Accuracy Rule-Based NLP Demo Engine (Offline & Reliable)
        return runRuleBasedDemoEngine(cleanSubject, cleanDesc, cleanName);
    }

    private AiSuggestionDto runRuleBasedDemoEngine(String subject, String description, String customerName) {
        String combined = (subject + " " + description).toLowerCase();

        // Category Classification via Keyword Scoring
        Map<Category, Integer> categoryScores = new EnumMap<>(Category.class);
        categoryScores.put(Category.TECHNICAL, countMatches(combined, "error", "crash", "bug", "exception", "failed", "broken", "500", "404", "timeout", "latency", "server", "database", "stacktrace", "nullpointer", "code", "api"));
        categoryScores.put(Category.BILLING, countMatches(combined, "charge", "refund", "invoice", "payment", "credit card", "subscription", "price", "pricing", "receipt", "deducted", "overcharge", "stripe", "paypal", "billing", "renew"));
        categoryScores.put(Category.ACCOUNT_ACCESS, countMatches(combined, "login", "password", "2fa", "otp", "reset", "locked", "credential", "authenticate", "mfa", "verification", "token", "access denied", "blocked account", "signup"));
        categoryScores.put(Category.FEATURE_REQUEST, countMatches(combined, "feature", "suggest", "request", "add", "enhance", "enhancement", "improvement", "would be great", "roadmap", "dark mode", "export", "option"));
        categoryScores.put(Category.GENERAL_INQUIRY, countMatches(combined, "question", "how to", "help", "guide", "info", "information", "inquiry", "support", "hello", "hi", "documentation"));

        Category bestCategory = Category.GENERAL_INQUIRY;
        int maxCategoryScore = 0;
        for (Map.Entry<Category, Integer> entry : categoryScores.entrySet()) {
            if (entry.getValue() > maxCategoryScore) {
                maxCategoryScore = entry.getValue();
                bestCategory = entry.getKey();
            }
        }

        // Priority Classification via Urgency & Impact Markers
        boolean criticalMatch = containsAny(combined, "outage", "production down", "system down", "security breach", "data loss", "catastrophic", "immediate emergency", "ransomware");
        boolean highMatch = containsAny(combined, "urgent", "asap", "blocking", "blocked", "cannot work", "cannot access", "deadline", "critical", "broken", "failed payment");
        boolean mediumMatch = containsAny(combined, "issue", "problem", "slow", "error", "delay", "glitch", "bug", "incorrect");

        Priority recommendedPriority;
        boolean urgentFlag = false;
        if (criticalMatch) {
            recommendedPriority = Priority.CRITICAL;
            urgentFlag = true;
        } else if (highMatch) {
            recommendedPriority = Priority.HIGH;
            urgentFlag = true;
        } else if (mediumMatch || maxCategoryScore > 1) {
            recommendedPriority = Priority.MEDIUM;
        } else {
            recommendedPriority = Priority.LOW;
        }

        double confidence = Math.min(0.96, 0.72 + (maxCategoryScore * 0.06));

        String reasoning = String.format(
                "Rule-Based Engine matched %d context keywords for %s with %s urgency markers.",
                maxCategoryScore, bestCategory.getDisplayName(), recommendedPriority.name()
        );

        String draftedResponse = generateDraftedResponse(customerName, bestCategory, recommendedPriority, subject);

        return new AiSuggestionDto(
                bestCategory,
                recommendedPriority,
                confidence,
                reasoning,
                draftedResponse,
                "RULE_BASED_DEMO",
                urgentFlag
        );
    }

    private String generateDraftedResponse(String customerName, Category category, Priority priority, String subject) {
        StringBuilder draft = new StringBuilder();
        draft.append("Hello ").append(customerName).append(",\n\n");
        draft.append("Thank you for reaching out to our Customer Support Team regarding \"").append(subject).append("\".\n\n");

        switch (category) {
            case BILLING:
                draft.append("We have received your billing inquiry and our finance specialists are reviewing the corresponding invoice and transaction records. ");
                draft.append("Please rest assured that any unauthorized or duplicate charge will be rectified immediately.\n\n");
                draft.append("To expedite the resolution, could you please confirm the last 4 digits of the payment method or transaction ID if available?");
                break;
            case TECHNICAL:
                draft.append("Our engineering support team has logged this issue and is examining system traces and server logs. ");
                if (priority == Priority.CRITICAL || priority == Priority.HIGH) {
                    draft.append("Because this has been identified as high priority, an on-call engineer has been alerted to prioritize this investigation.\n\n");
                } else {
                    draft.append("We are reproducing the workflow in our sandbox environment.\n\n");
                }
                draft.append("Could you kindly share your browser version, operating system, and any error codes displayed on your screen?");
                break;
            case ACCOUNT_ACCESS:
                draft.append("We understand how crucial account access is. For security protocols, we have initiated an identity verification check. ");
                draft.append("Please check your registered inbox for a secure one-time reset link (valid for 15 minutes).\n\n");
                draft.append("If you still cannot log in, please let us know if you see a specific lockout notice.");
                break;
            case FEATURE_REQUEST:
                draft.append("Thank you for sharing your feedback! Product ideas like yours directly help us improve our platform. ");
                draft.append("We have forwarded this suggestion to our Product Management team for consideration in upcoming sprint roadmaps.");
                break;
            case GENERAL_INQUIRY:
            default:
                draft.append("We are glad to assist you. Our support team is actively reviewing your request and will provide full details shortly.\n\n");
                draft.append("Please let us know if you have any additional context to add in the meantime.");
                break;
        }

        draft.append("\n\nWarm regards,\nAlex Rivera\nCustomer Support Specialist\nSkillWallet HelpDesk Team");
        return draft.toString();
    }

    private AiSuggestionDto callGeminiApi(String subject, String description, String customerName) {
        String url = String.format("%s/%s:generateContent?key=%s", geminiApiUrl, geminiModel, geminiApiKey);

        String prompt = "You are an AI support triage assistant for a customer ticketing system. " +
                "Analyze the following support ticket:\n" +
                "Customer Name: " + customerName + "\n" +
                "Subject: " + subject + "\n" +
                "Description: " + description + "\n\n" +
                "Respond ONLY with a valid JSON object matching this exact structure:\n" +
                "{\n" +
                "  \"category\": \"TECHNICAL\" | \"BILLING\" | \"ACCOUNT_ACCESS\" | \"FEATURE_REQUEST\" | \"GENERAL_INQUIRY\",\n" +
                "  \"priority\": \"LOW\" | \"MEDIUM\" | \"HIGH\" | \"CRITICAL\",\n" +
                "  \"confidence\": 0.95,\n" +
                "  \"reasoning\": \"Brief 1-sentence reason\",\n" +
                "  \"draftedResponse\": \"A courteous, helpful, professional reply to the customer\"\n" +
                "}";

        Map<String, Object> part = Collections.singletonMap("text", prompt);
        Map<String, Object> content = Collections.singletonMap("parts", Collections.singletonList(part));
        Map<String, Object> requestBody = Collections.singletonMap("contents", Collections.singletonList(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode textNode = root.path("candidates").get(0).path("content").path("parts").get(0).path("text");
                if (!textNode.isMissingNode()) {
                    String rawText = textNode.asText().trim();
                    // Strip markdown fences if present
                    if (rawText.startsWith("```json")) {
                        rawText = rawText.substring(7);
                    }
                    if (rawText.startsWith("```")) {
                        rawText = rawText.substring(3);
                    }
                    if (rawText.endsWith("```")) {
                        rawText = rawText.substring(0, rawText.length() - 3);
                    }
                    rawText = rawText.trim();

                    JsonNode parsed = objectMapper.readTree(rawText);
                    Category cat = Category.valueOf(parsed.path("category").asText("GENERAL_INQUIRY"));
                    Priority prio = Priority.valueOf(parsed.path("priority").asText("MEDIUM"));
                    double conf = parsed.path("confidence").asDouble(0.92);
                    String reason = parsed.path("reasoning").asText("Classified via Gemini AI LLM model.");
                    String draft = parsed.path("draftedResponse").asText();

                    return new AiSuggestionDto(cat, prio, conf, reason, draft, "GEMINI_LIVE", prio == Priority.CRITICAL || prio == Priority.HIGH);
                }
            } catch (Exception e) {
                log.warn("Failed to parse Gemini response: {}. Falling back to rule-based engine.", e.getMessage());
            }
        }
        return null;
    }

    private int countMatches(String text, String... keywords) {
        int count = 0;
        for (String kw : keywords) {
            if (text.contains(kw)) {
                count++;
            }
        }
        return count;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) {
                return true;
            }
        }
        return false;
    }
}

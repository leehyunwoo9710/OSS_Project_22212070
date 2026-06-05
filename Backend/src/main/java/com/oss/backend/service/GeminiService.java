package com.oss.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 약품 이미지 OCR 인식 로직.
     * Google Gemini API를 호출하여 이미지에 적힌 약품명 키워드를 추출합니다.
     * 
     * @param file 사용자가 업로드한 약품 이미지 파일
     * @return 추출된 약품명 문자열
     * @throws Exception API 호출 실패 또는 결과 파싱 중 오류 발생 시
     */
    public String extractMedicineNameFromImage(MultipartFile file) throws Exception {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key="
                + geminiApiKey;

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        String mimeType = file.getContentType();
        if (mimeType == null) {
            mimeType = "image/jpeg";
        }

        Map<String, Object> requestBody = buildPrompt(base64Image, mimeType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    String extractedText = (String) parts.get(0).get("text");
                    return extractedText != null ? extractedText.trim() : null;
                }
            }
        }
        throw new RuntimeException("제미나이 API 호출 실패 또는 결과가 없습니다.");
    }

    /**
     * Gemini API 요청을 위한 프롬프트 및 페이로드 구성.
     * 약 이름만 정확히 추출하도록 구체적인 지시사항을 포함합니다.
     * 
     * @param base64Image Base64로 인코딩된 이미지 데이터
     * @param mimeType 이미지의 MIME 타입 (예: image/jpeg)
     * @return API에 전송할 JSON 요청 바디 맵
     */
    private Map<String, Object> buildPrompt(String base64Image, String mimeType) {
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text",
                "이 이미지 속에 적힌 핵심적인 약 이름 하나만 정확히 파악해서 알려줘. 제품의 전체 이름 대신 제일 중요한 약품명 키워드만 뽑아. 부가적인 설명이나 마침표, 줄바꿈은 절대로 붙이지 말고 오직 단어만 출력해.");

        Map<String, Object> inlineData = new HashMap<>();
        inlineData.put("mimeType", mimeType);
        inlineData.put("data", base64Image);

        Map<String, Object> imagePart = new HashMap<>();
        imagePart.put("inlineData", inlineData);

        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(textPart);
        parts.add(imagePart);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", parts);

        List<Map<String, Object>> contents = new ArrayList<>();
        contents.add(content);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", contents);

        return requestBody;
    }
}

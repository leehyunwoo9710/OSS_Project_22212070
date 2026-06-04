package com.oss.backend.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ClassPathResource;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import com.oss.backend.dto.DrugInfoResponseDto;

@Service
public class OCRProcessor {

    @Value("${google.vision.credentials.json}")
    private String credentialsJson;

    @Autowired
    private MedicineAPI MedicineAPI;

    public String extractTextFromImage(MultipartFile file) throws IOException {
        GoogleCredentials credentials;
        try (InputStream keyStream = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))) {
            credentials = GoogleCredentials.fromStream(keyStream);
        } catch (IOException e) {
            throw new RuntimeException("Google Vision API 인증 정보를 처리할 수 없습니다.", e);
        }

        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());
            Image img = Image.newBuilder().setContent(imgBytes).build();

            Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    throw new RuntimeException("OCR Error: " + res.getError().getMessage());
                }

                if (res.getFullTextAnnotation() == null) {
                    return "인식된 텍스트가 없습니다.";
                }

                String fullText = res.getFullTextAnnotation().getText();
                return parseDrugName(fullText);
            }
        }
        return "인식된 텍스트가 없습니다.";
    }

    private String parseDrugName(String rawText) {
        if (rawText == null || rawText.isEmpty())
            return "";
            
        String[] lines = rawText.split("\n");
        
        for (String line : lines) {
            String trimmed = line.trim();
            String normalizedTrimmed = trimmed.replaceAll("\\s+", "");
            
            // 1차 필터링: 불필요한 단어 및 너무 짧은 단어 제외 (정확한 금지어 필터링 추가)
            if (normalizedTrimmed.length() <= 1 || 
                normalizedTrimmed.matches(".*[0-9]+(mg|밀리그램|ml).*") || 
                normalizedTrimmed.matches("^[0-9]+$") || // 오직 숫자로만 이루어진 줄
                normalizedTrimmed.contains("밀리그램") ||
                normalizedTrimmed.equals("밀리") ||
                normalizedTrimmed.equals("그램") ||
                normalizedTrimmed.equals("효능") ||
                normalizedTrimmed.equals("효과") ||
                normalizedTrimmed.equals("용법") ||
                normalizedTrimmed.equals("용량") ||
                normalizedTrimmed.equals("주의사항") ||
                normalizedTrimmed.contains("제조") || 
                normalizedTrimmed.contains("약국") ||
                normalizedTrimmed.contains("일반의약품") ||
                normalizedTrimmed.contains("전문의약품") ||
                normalizedTrimmed.equals("정") || 
                normalizedTrimmed.equals("연질캡슐") || 
                normalizedTrimmed.equals("캡슐")) {
                continue;
            }

            // 2차 검증: 공공데이터 API에 검색하여 유효한 약인지 확인
            try {
                // OCR 오인식 방지를 위해 뒷부분의 "정", "장", "캡슐", "시럽" 등을 잘라내고 검색 시도
                String searchKeyword = normalizedTrimmed;
                if (searchKeyword.length() > 2) {
                    if (searchKeyword.endsWith("정") || searchKeyword.endsWith("장") || searchKeyword.endsWith("산")) {
                        searchKeyword = searchKeyword.substring(0, searchKeyword.length() - 1);
                    } else if (searchKeyword.endsWith("캡슐") || searchKeyword.endsWith("시럽")) {
                        searchKeyword = searchKeyword.substring(0, searchKeyword.length() - 2);
                    }
                }

                DrugInfoResponseDto response = MedicineAPI.fetchDrugInfo(searchKeyword, 1, 10, false);
                if (response != null && response.getBody() != null 
                    && response.getBody().getItems() != null 
                    && !response.getBody().getItems().isEmpty()) {
                    
                    String fallbackMatch = null;

                    for (DrugInfoResponseDto.Item item : response.getBody().getItems()) {
                        if (item.getItemName() != null) {
                            String apiItemNameNormalized = item.getItemName().replaceAll("\\s+", "");
                            
                            // 1순위: 약 이름이 검색어로 '시작'하는 경우 (가장 정확한 매칭)
                            if (apiItemNameNormalized.startsWith(searchKeyword)) {
                                return item.getItemName();
                            }
                            
                            // 2순위: 약 이름에 검색어가 '포함'되어 있는 경우 (제조사 이름이 앞에 붙은 경우 등 대비)
                            if (fallbackMatch == null && apiItemNameNormalized.contains(searchKeyword)) {
                                fallbackMatch = item.getItemName();
                            }
                        }
                    }
                    
                    // 시작하는(startsWith) 약은 없었지만 포함된(contains) 약이 있다면 그것을 반환
                    if (fallbackMatch != null) {
                        return fallbackMatch;
                    }
                }
            } catch (Exception e) {
                // API 호출 중 오류 발생 시, 해당 단어는 건너뛰고 다음 단어 검사
                continue;
            }
        }
        
        // API에서 하나도 일치하는 것을 찾지 못했다면, 필터링을 거친 첫 번째 유효 단어를 반환
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 1 && !trimmed.matches(".*[0-9]+(mg|밀리그램|ml).*") && !trimmed.contains("밀리그램") && !trimmed.contains("제조") && !trimmed.contains("약국") && !trimmed.contains("일반의약품")) {
                return trimmed;
            }
        }
        
        return lines[0].trim();
    }
}

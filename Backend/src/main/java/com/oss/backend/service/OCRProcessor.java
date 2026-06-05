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

        // 1. 단어 단위로 분리 (뛰어쓰기 및 줄바꿈 기준)
        String[] rawWords = rawText.split("\\s+");
        List<String> searchCandidates = new ArrayList<>();

        // 2. 검색을 위한 불용어(Stopwords) 필터링 (순수 키워드만 추출)
        for (String word : rawWords) {
            String trimmed = word.trim().replaceAll("[^가-힣a-zA-Z0-9]", ""); // 특수기호 제거
            if (trimmed.length() < 2 || trimmed.matches("^[0-9a-zA-Z]+$")) continue;

            // 금지어 (검색어로 쓰이면 안 되는 너무 흔한 단어들)
            if (trimmed.contains("효능") || trimmed.contains("효과") || trimmed.contains("용법") ||
                trimmed.contains("용량") || trimmed.contains("주의") || trimmed.contains("부작용") ||
                trimmed.contains("제약") || trimmed.contains("약품") || trimmed.contains("성분") ||
                trimmed.contains("원료") || trimmed.contains("제조") || trimmed.contains("판매") ||
                trimmed.contains("보관") || trimmed.contains("상담") || trimmed.contains("주식회사") ||
                trimmed.contains("소비자") || trimmed.contains("보호") || trimmed.contains("처방") ||
                trimmed.contains("보존") || trimmed.contains("첨가") || trimmed.contains("기한") ||
                trimmed.contains("밀리") || trimmed.contains("그램") || trimmed.contains("mg") ||
                trimmed.contains("ml") || trimmed.equals("어린이") || trimmed.equals("성인") ||
                trimmed.contains("안전상비") || trimmed.equals("일반") || trimmed.equals("전문") ||
                trimmed.contains("수입") || trimmed.contains("에탄올") || trimmed.contains("나트륨") ||
                trimmed.contains("진통") || trimmed.contains("해열") || trimmed.contains("소염") ||
                trimmed.contains("감기") || trimmed.contains("기침") || trimmed.contains("두통") ||
                trimmed.contains("약국")) {
                continue;
            }
            searchCandidates.add(trimmed);
        }

        if (searchCandidates.isEmpty()) {
            return fallbackParse(rawText);
        }

        // 3. API 검색 및 채점(Scoring) 로직
        String bestMatchName = null;
        int highestScore = -1;

        // 최대 3개의 유력한 단어 후보로 검색 시도
        int searchLimit = Math.min(3, searchCandidates.size());

        for (int i = 0; i < searchLimit; i++) {
            String searchKeyword = searchCandidates.get(i);

            // 검색 정확도를 위해 끝에 붙은 제형 제거
            if (searchKeyword.length() > 2) {
                if (searchKeyword.endsWith("정") || searchKeyword.endsWith("장") || searchKeyword.endsWith("산")) {
                    searchKeyword = searchKeyword.substring(0, searchKeyword.length() - 1);
                } else if (searchKeyword.endsWith("캡슐") || searchKeyword.endsWith("시럽")) {
                    searchKeyword = searchKeyword.substring(0, searchKeyword.length() - 2);
                }
            }

            try {
                DrugInfoResponseDto response = MedicineAPI.fetchDrugInfo(searchKeyword, 1, 15, false);
                if (response != null && response.getBody() != null && response.getBody().getItems() != null) {

                    for (DrugInfoResponseDto.Item item : response.getBody().getItems()) {
                        if (item.getItemName() == null) continue;

                        String apiItemName = item.getItemName().replaceAll("\\s+", "");
                        int score = 0;

                        // 채점(Scoring): OCR 원본 텍스트의 파편들이 이 약품명에 얼마나 많이 녹아있는지 검사
                        // 예: OCR에 "어린이", "부루펜", "시럽" 이 있었다면 각각 점수 합산 (+8점)
                        for (String ocrWord : rawWords) {
                            String cleanWord = ocrWord.trim().replaceAll("[^가-힣a-zA-Z0-9]", "");
                            if (cleanWord.length() >= 2 && apiItemName.contains(cleanWord)) {
                                score += cleanWord.length(); // 단어 길이를 가중치로 부여
                            }
                        }

                        // 최고 점수를 받은 약품을 기억
                        if (score > highestScore) {
                            highestScore = score;
                            bestMatchName = item.getItemName();
                        }
                    }
                }
            } catch (Exception e) {
                // API 호출 에러 시 다음 단어로 넘어감
                continue;
            }

            // 첫 번째 검색어로 충분히 확신할 만한(점수가 높은) 결과가 나왔다면 조기 종료
            if (highestScore >= searchKeyword.length()) {
                break;
            }
        }

        if (bestMatchName != null) {
            return bestMatchName;
        }

        // 4. API 매칭 실패 시 폴백
        return fallbackParse(rawText);
    }

    private String fallbackParse(String rawText) {
        String[] lines = rawText.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 1 && !trimmed.matches(".*[0-9]+(mg|밀리그램|ml).*") &&
                !trimmed.contains("밀리그램") && !trimmed.contains("제조") &&
                !trimmed.contains("약국") && !trimmed.contains("일반의약품")) {
                return trimmed;
            }
        }
        return lines[0].trim();
    }
}

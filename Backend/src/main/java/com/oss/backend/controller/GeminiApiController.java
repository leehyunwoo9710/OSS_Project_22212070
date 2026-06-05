package com.oss.backend.controller;

import com.oss.backend.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ocr")
public class GeminiApiController {

    private final GeminiService geminiService;

    public GeminiApiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    /**
     * 약품 이미지 OCR 인식 API.
     * 사용자가 업로드한 약품 사진을 Gemini AI로 분석하여 약품 이름을 텍스트로 추출합니다.
     * 
     * @param image 분석할 약품 사진 (MultipartFile 형식)
     * @return 추출된 약품 이름 텍스트 (또는 에러 메시지)
     */
    @PostMapping("/scan")
    public ResponseEntity<?> scanImage(@RequestParam("image") MultipartFile image) {
        try {
            String text = geminiService.extractMedicineNameFromImage(image);
            Map<String, String> response = new HashMap<>();
            response.put("text", text);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }
}

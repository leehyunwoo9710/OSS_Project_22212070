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

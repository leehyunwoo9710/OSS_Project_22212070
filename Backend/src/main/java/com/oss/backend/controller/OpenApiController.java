package com.oss.backend.controller;

import com.oss.backend.dto.DrugInfoResponseDto;
import com.oss.backend.service.MedicineAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/drug")
public class OpenApiController {

    private final MedicineAPI MedicineAPI;

    public OpenApiController(MedicineAPI MedicineAPI) {
        this.MedicineAPI = MedicineAPI;
    }

    @GetMapping("/search")
    public ResponseEntity<DrugInfoResponseDto> searchDrugInfo(
            @RequestParam("itemName") String itemName,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(defaultValue = "false") boolean isChild
    ) {
        DrugInfoResponseDto data = MedicineAPI.fetchDrugInfo(itemName, pageNo, numOfRows, isChild);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/recommend")
    public ResponseEntity<DrugInfoResponseDto> recommendDrugInfo(
            @RequestParam("symptom") String symptom,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(defaultValue = "false") boolean isChild
    ) {
        DrugInfoResponseDto data = MedicineAPI.fetchDrugBySymptom(symptom, pageNo, numOfRows, isChild);
        return ResponseEntity.ok(data);
    }
}

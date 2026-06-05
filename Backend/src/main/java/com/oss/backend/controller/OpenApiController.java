package com.oss.backend.controller;

import com.oss.backend.dto.DrugInfoResponseDto;
import com.oss.backend.service.MedicineAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/drug")
public class OpenApiController {

    private final MedicineAPI MedicineAPI;

    public OpenApiController(MedicineAPI MedicineAPI) {
        this.MedicineAPI = MedicineAPI;
    }

    /**
     * 약품명 기반 정보 검색 API.
     * 공공데이터포털(OpenAPI)을 활용하여 약품 이름으로 상세 정보를 검색합니다.
     * 
     * @param itemName 검색할 약품명
     * @param pageNo 페이지 번호 (기본값 1)
     * @param numOfRows 한 페이지당 결과 수 (기본값 10)
     * @param isChild 어린이용 약품(e약은요 API) 검색 여부
     * @return 약품 상세 정보 DTO
     */
    @GetMapping("/search")
    public ResponseEntity<DrugInfoResponseDto> searchDrugInfo(
            @RequestParam("itemName") String itemName,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(defaultValue = "false") boolean isChild) {
        DrugInfoResponseDto data = MedicineAPI.fetchDrugInfo(itemName, pageNo, numOfRows, isChild);
        return ResponseEntity.ok(data);
    }

    /**
     * 증상 기반 약품 추천 API.
     * 공공데이터포털(OpenAPI) 효능/효과 필드를 기반으로 특정 증상에 맞는 약품을 검색합니다.
     * 
     * @param symptom 검색할 증상 (예: 두통, 복통)
     * @param pageNo 페이지 번호 (기본값 1)
     * @param numOfRows 한 페이지당 결과 수 (기본값 10)
     * @param isChild 어린이용 약품(e약은요 API) 검색 여부
     * @return 추천 약품 상세 정보 DTO
     */
    @GetMapping("/recommend")
    public ResponseEntity<DrugInfoResponseDto> recommendDrugInfo(
            @RequestParam("symptom") String symptom,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(defaultValue = "false") boolean isChild) {
        DrugInfoResponseDto data = MedicineAPI.fetchDrugBySymptom(symptom, pageNo, numOfRows, isChild);
        return ResponseEntity.ok(data);
    }
}

package com.oss.backend.service;

import com.oss.backend.dto.DrugInfoResponseDto;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MedicineAPI {

    private final RestTemplate restTemplate;

    @Value("${openapi.service-key}")
    private String serviceKey;

    @Value("${openapi.base-url:http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList}")
    private String baseUrl;

    public MedicineAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 약품명 기반 검색 (기본 페이징 및 성인용 조건).
     * 
     * @param itemName 검색할 약품명
     * @return 검색된 약품 상세 정보
     */
    public DrugInfoResponseDto fetchDrugInfo(String itemName) {
        return fetchDrugInfo(itemName, 1, 10, false);
    }

    /**
     * 약품명 기반 상세 검색.
     * 외부 공공데이터 OpenAPI를 호출하여 약품 정보를 가져옵니다.
     * 
     * @param itemName 검색할 약품명
     * @param pageNo 페이지 번호
     * @param numOfRows 한 페이지당 데이터 개수
     * @param isChild 어린이용 약품 여부 필터링
     * @return 검색된 약품 상세 정보
     */
    public DrugInfoResponseDto fetchDrugInfo(String itemName, int pageNo, int numOfRows, boolean isChild) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("itemName", itemName)
                .queryParam("type", "json")
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        DrugInfoResponseDto response = restTemplate.getForObject(uri, DrugInfoResponseDto.class);
        return filterByChild(response, isChild);
    }

    /**
     * 증상 기반 약품 추천 검색.
     * 외부 공공데이터 OpenAPI의 효능/효과 필드를 기반으로 약품을 검색합니다.
     * 
     * @param symptom 검색할 증상 키워드 (예: 두통, 복통)
     * @param pageNo 페이지 번호
     * @param numOfRows 한 페이지당 데이터 개수
     * @param isChild 어린이용 약품 여부 필터링
     * @return 증상에 맞는 약품 목록
     */
    public DrugInfoResponseDto fetchDrugBySymptom(String symptom, int pageNo, int numOfRows, boolean isChild) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("efcyQesitm", symptom)
                .queryParam("type", "json")
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        DrugInfoResponseDto response = restTemplate.getForObject(uri, DrugInfoResponseDto.class);
        return filterByChild(response, isChild);
    }

    /**
     * 어린이용 약품 필터링 헬퍼 메서드.
     * 제품명에 '어린이' 키워드가 포함되었는지 여부로 필터링을 수행합니다.
     * 
     * @param response OpenAPI 응답 DTO
     * @param isChild true면 어린이용만, false면 성인용만 남김
     * @return 필터링이 완료된 응답 DTO
     */
    private DrugInfoResponseDto filterByChild(DrugInfoResponseDto response, boolean isChild) {
        if (response != null && response.getBody() != null && response.getBody().getItems() != null) {
            java.util.List<DrugInfoResponseDto.Item> filtered = response.getBody().getItems().stream()
                    .filter(item -> {
                        boolean hasChildWord = item.getItemName() != null && item.getItemName().contains("어린이");
                        return isChild ? hasChildWord : !hasChildWord;
                    })
                    .collect(java.util.stream.Collectors.toList());
            response.getBody().setItems(filtered);
        }
        return response;
    }
}

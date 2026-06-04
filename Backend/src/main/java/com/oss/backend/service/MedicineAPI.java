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

    public DrugInfoResponseDto fetchDrugInfo(String itemName) {
        return fetchDrugInfo(itemName, 1, 10, false);
    }

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

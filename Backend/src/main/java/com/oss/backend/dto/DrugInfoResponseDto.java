package com.oss.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DrugInfoResponseDto {
    private Header header;
    private Body body;

    @Getter
    @Setter
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    @Setter
    public static class Body {
        private int pageNo;
        private int totalCount;
        private int numOfRows;
        private List<Item> items;
    }

    @Getter
    @Setter
    public static class Item {
        private String itemName;        // 약 이름
        private String itemSeq;         // 약품 품목 기준 코드
        private String efcyQesitm;      // 약의 효능/증상
        private String useMethodQesitm; // 복용법/사용법
        private String atpnQesitm;      // 주의사항
        private String seQesitm;        // 부작용
        private String depositMethodQesitm; // 보관법
        private String intrcQesitm;     // 상호작용
    }
}

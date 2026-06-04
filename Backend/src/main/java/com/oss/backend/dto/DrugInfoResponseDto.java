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
        private String itemName;        // ??
        private String itemSeq;         // ??
        private String efcyQesitm;      // 1, ?
        private String useMethodQesitm; // 2, ??
        private String atpnQesitm;      // 4, ?
        private String seQesitm;        // 6, ?
    }
}

package com.oss.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DrugCartRequestDto {
    private String itemName;
    private String itemSeq;
    private String efcyQesitm;
    private String useMethodQesitm;
    private String atpnQesitm;
    private String seQesitm;
}

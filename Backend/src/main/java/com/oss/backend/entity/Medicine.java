package com.oss.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;        // 약 이름
    private String itemSeq;         // 약품 품목 기준 코드
    
    // 증상/효능 관련 정보 (TEXT)
    @Column(columnDefinition = "TEXT")
    private String efcyQesitm;      // 약의 효능/증상
    
    @Column(columnDefinition = "TEXT")
    private String useMethodQesitm; // 복용법/사용법
    
    @Column(columnDefinition = "TEXT")
    private String atpnQesitm;      // 4, ?
    
    @Column(columnDefinition = "TEXT")
    private String seQesitm;        // 6, ?

    @Column(nullable = false)
    private String username;        // 해당 약을 담은 사용자 이름

    public Medicine(String itemName, String itemSeq, String efcyQesitm, String useMethodQesitm, String atpnQesitm, String seQesitm, String username) {
        this.itemName = itemName;
        this.itemSeq = itemSeq;
        this.efcyQesitm = efcyQesitm;
        this.useMethodQesitm = useMethodQesitm;
        this.atpnQesitm = atpnQesitm;
        this.seQesitm = seQesitm;
        this.username = username;
    }
}

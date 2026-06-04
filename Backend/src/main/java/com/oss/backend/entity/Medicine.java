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

    private String itemName;        // ??
    private String itemSeq;         // ??
    
    // ????????TEXT ????
    @Column(columnDefinition = "TEXT")
    private String efcyQesitm;      // 1, ?
    
    @Column(columnDefinition = "TEXT")
    private String useMethodQesitm; // 2, ??
    
    @Column(columnDefinition = "TEXT")
    private String atpnQesitm;      // 4, ?
    
    @Column(columnDefinition = "TEXT")
    private String seQesitm;        // 6, ?

    @Column(nullable = false)
    private String username;        // ??????

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

package com.artivefor.me.data.artwork;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @NoArgsConstructor
public class ArtworkTranslation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 작품 제목 (한글/영문 등)

    @Column(columnDefinition = "TEXT")
    private String description; // 작품 상세 설명

    private boolean isAutomated = false; // 자동 번역 여부
}
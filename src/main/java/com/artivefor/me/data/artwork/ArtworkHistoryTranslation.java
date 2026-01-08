package com.artivefor.me.data.artwork;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @NoArgsConstructor
public class ArtworkHistoryTranslation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;   // 히스토리 제목 (예: "유튜브 작업 브이로그")

    @Column(columnDefinition = "TEXT")
    private String content; // 히스토리 상세 내용 (작업 일지)

    private boolean isAutomated = false; // 자동 번역 여부
}
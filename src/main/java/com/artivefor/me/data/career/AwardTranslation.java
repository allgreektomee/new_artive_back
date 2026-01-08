package com.artivefor.me.data.career;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter @NoArgsConstructor
@AllArgsConstructor
public class AwardTranslation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 수상 명칭 (예: 대상, 특선, 제 n회 OO 공모전)
    private String title;

    // 수여 기관 (예: 문화체육관광부, OO 갤러리)
    private String organization;

    // 수상 혜택이나 상세 내용 (필요 시)
    @Column(columnDefinition = "TEXT")
    private String description;

    // 자동 번역 여부 플래그
    private boolean isAutomated = false;

    // 생성자 (ID 제외)
    public AwardTranslation(String title, String organization, String description) {
        this.title = title;
        this.organization = organization;
        this.description = description;
    }
}
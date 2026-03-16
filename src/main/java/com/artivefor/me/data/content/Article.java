package com.artivefor.me.data.content;

import com.artivefor.me.data.common.BaseTimeEntity;
import com.artivefor.me.data.common.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "articles")
public class Article extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type; // INSIGHT or LOG

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore // ? 추가: JSON 변환 시 카테고리 내부에서 아티클을 다시 참조하는 루프를 끊음
    private Category category;

    private String koTitle;
    private String enTitle;

    @Column(columnDefinition = "TEXT")
    private String summary; // 요약문

    @Column(columnDefinition = "TEXT")
    private String contents; // 본문 (직접 작성 시)

    private String externalUrl; // 워드프레스 링크
    private String thumbnailUrl; // 썸네일 이미지

    @Builder.Default
    private boolean isPublic = true;

    // 수정 편의 메서드
    public void update(Category category, String koTitle, String enTitle, String summary, String externalUrl, String thumbnailUrl) {
        this.category = category;
        this.koTitle = koTitle;
        this.enTitle = enTitle;
        this.summary = summary;
        this.externalUrl = externalUrl;
        this.thumbnailUrl = thumbnailUrl;
    }
}

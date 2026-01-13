package com.artivefor.me.data.artwork; // artwork 패키지 내 혹은 하위로 관리 권장

import com.artivefor.me.data.common.BaseTimeEntity;
import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.common.Visibility;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Builder // 빌더 추가
@AllArgsConstructor // 빌더 사용을 위해 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtworkHistory extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    // ✅ 이미지 리스트 대신 단일 필드로 변경
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private HistoryType type; // MANUAL, YOUTUBE 등

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC;

    // ✅ 번역도 히스토리 내부에 맵으로 직접 관리 (히스토리 전용 번역 엔티티 사용)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "history_id")
    @MapKeyColumn(name = "lang_code")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Map<LanguageCode, ArtworkHistoryTranslation> translations = new HashMap<>();

    // 연관관계 편의 메서드
    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    public void addTranslation(LanguageCode lang, ArtworkHistoryTranslation translation) {
        if (this.translations == null) {
            this.translations = new HashMap<>();
        }
        this.translations.put(lang, translation);
    }

    public void update(String imageUrl) {
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }
}
package com.artivefor.me.data.career;

import com.artivefor.me.data.common.BaseTimeEntity;
import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.data.artwork.Artwork;
import com.artivefor.me.data.common.LanguageCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter @NoArgsConstructor
public class Award extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 수상 시점 (보통 연도나 정확한 날짜를 사용)
    private String awardYear;

    // 상장이나 훈장 사진 URL
    private String thumbnailUrl;

    // [관계] 이 수상을 기록한 작가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ArtiveUser user;

    // [관계] 특정 작품으로 상을 받았다면 해당 작품과 연결 (없을 수 있음)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    private Artwork relatedArtwork;

    /**
     * 다국어 번역 데이터 (AwardTranslation)
     * KO: { title: "대한민국 미술대전", organization: "한국미술협회" }
     * EN: { title: "Grand Art Exhibition of Korea", organization: "IAA Korea" }
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "award_id")
    @MapKeyColumn(name = "lang_code")
    @Enumerated(EnumType.STRING)
    private Map<LanguageCode, AwardTranslation> translations = new HashMap<>();


}
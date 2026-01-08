package com.artivefor.me.data.reference;


import com.artivefor.me.data.artwork.Artwork;
import com.artivefor.me.data.artwork.ArtworkHistory;
import com.artivefor.me.data.common.BaseTimeEntity;
import com.artivefor.me.data.common.LanguageCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter @NoArgsConstructor
public class Reference extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mediaUrl;   // 참고 이미지나 영상 썸네일
    private String originalLink; // 출처 원본 링크
    private String source;     // 출처 명칭 (예: 핀터레스트, 작가이름 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id")
    private ArtworkHistory history; // 특정 작업 기록과 연결 가능

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reference_id")
    @MapKeyColumn(name = "lang_code")
    @Enumerated(EnumType.STRING)
    private Map<LanguageCode, ReferenceTranslation> translations = new HashMap<>();
}
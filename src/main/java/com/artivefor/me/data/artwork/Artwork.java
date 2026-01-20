package com.artivefor.me.data.artwork;

import com.artivefor.me.data.common.Visibility;
import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.data.common.BaseTimeEntity;
import com.artivefor.me.data.common.LanguageCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Builder // 추가
@AllArgsConstructor // 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Artwork extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 시스템 등록일과 별개로 관리하는 실제 작업 기간
    private LocalDate startedAt;  // 작업 시작일
    private LocalDate finishedAt; // 완성일

    @Enumerated(EnumType.STRING)
    private WorkStatus status;    // IN_PROGRESS, COMPLETED 등

    private String thumbnailUrl;
    private String medium; // 재료
    private String size;   // 규격

    @ElementCollection
    @CollectionTable(
            name = "artwork_images",
            joinColumns = @JoinColumn(name = "artwork_id")
    )
    @Column(name = "image_url")
    @OrderColumn(name = "list_order") // 🚀 중요: 드래그 앤 드롭 순서 저장
    private List<String> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ArtiveUser author;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "artwork_id")
    @MapKeyColumn(name = "lang_code")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Map<LanguageCode, ArtworkTranslation> translations = new HashMap<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC; // 작품 전체의 공개 여부

    @Builder.Default
    @OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtworkHistory> histories = new ArrayList<>();

    /**
     * 다국어 정보를 추가하기 위한 편의 메서드
     */
    public void addTranslation(LanguageCode lang, ArtworkTranslation translation) {
        if (this.translations == null) {
            this.translations = new HashMap<>();
        }
        this.translations.put(lang, translation);
    }

    public void updateInfo(WorkStatus status, String medium, String size) {
        if (status != null) {
            this.status = status;
        }
        if (medium != null) {
            this.medium = medium;
        }
        if (size != null) {
            this.size = size;
        }
    }
}
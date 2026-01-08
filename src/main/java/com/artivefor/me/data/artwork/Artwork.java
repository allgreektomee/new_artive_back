package com.artivefor.me.data.artwork;

import com.artivefor.me.data.common.Visibility;
import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.data.common.BaseTimeEntity;
import com.artivefor.me.data.common.LanguageCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter @NoArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ArtiveUser author;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "artwork_id")
    @MapKeyColumn(name = "lang_code")
    @Enumerated(EnumType.STRING)
    private Map<LanguageCode, ArtworkTranslation> translations = new HashMap<>();

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC; // 작품 전체의 공개 여부
}
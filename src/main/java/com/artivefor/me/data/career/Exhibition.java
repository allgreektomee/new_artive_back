package com.artivefor.me.data.career;

import com.artivefor.me.data.common.BaseTimeEntity;
import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.user.ArtiveUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter @NoArgsConstructor
public class Exhibition extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ArtiveUser author;


    private String thumbnailUrl;
    private String startDate; // 시기 정보
    private String endDate;
    private String locationUrl; // 전시 장소 링크 (구글맵 등)

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "exhibition_id")
    @MapKeyColumn(name = "lang_code")
    @Enumerated(EnumType.STRING)
    private Map<LanguageCode, ExhibitionTranslation> translations = new HashMap<>();
}
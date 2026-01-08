package com.artivefor.me.data.career;

import com.artivefor.me.data.common.BaseTranslation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @NoArgsConstructor
public class ExhibitionTranslation extends BaseTranslation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // 전시 명칭
    private String locationName; // 전시 장소명

    @Column(columnDefinition = "TEXT")
    private String description;  // 전시 상세 설명
}
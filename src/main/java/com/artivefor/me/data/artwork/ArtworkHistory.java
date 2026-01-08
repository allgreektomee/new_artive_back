package com.artivefor.me.data.artwork; // artwork 패키지 내 혹은 하위로 관리 권장

import com.artivefor.me.data.common.BaseTimeEntity;
import com.artivefor.me.data.common.Visibility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @NoArgsConstructor
public class ArtworkHistory extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC; // [반영] 히스토리별 공개 범위

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC") // [반영] DB 조회 시 자동 정렬
    private List<ArtworkHistoryImage> images = new ArrayList<>();
}
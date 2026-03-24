package com.artivefor.me.data.index;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class IndexPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- [상단 헤더 설정] ---
    private String issueNo;           // VOL. 01
    private String backgroundColor;   // 배경색 (#F9F9F7)

    private String titleTag;          // PROLOGUE
    private String titleTagColor;     // 태그 색상

    @Column(length = 1000)
    private String mainTitle;         // 침묵의 기술: 공간이 말을 걸 때
    private String mainTitleColor;

    @Column(columnDefinition = "TEXT")
    private String description;       // 서사적 서문
    private String descriptionColor;

    // --- [하단 리스트 대표 설정] ---
    private String contentsTag;       // CONTENTS
    private String contentsTagColor;

    private String contentsNoColor;    // 리스트 숫자 색상
    private String contentsTitleColor; // 리스트 제목 색상
    private String contentsSubColor;   // 리스트 부제 색상

    @OneToMany(mappedBy = "indexPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndexItem> items = new ArrayList<>();
}


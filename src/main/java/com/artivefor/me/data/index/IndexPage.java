package com.artivefor.me.dto.IndexPage

@Entity
@Getter @Setter
public class IndexPage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

// IndexItem.java (하단 가변 리스트 항목)
@Entity
@Getter @Setter
public class IndexItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String no;                // 01, 02...
    private String subject;           // MAIN EXHIBITION
    private String subSubject;        // 빛과 그림자의 변주곡
    private String linkUrl;           // 이동 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_page_id")
    private IndexPage indexPage;
}
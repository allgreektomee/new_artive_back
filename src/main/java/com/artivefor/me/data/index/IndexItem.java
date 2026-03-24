package com.artivefor.me.data.index;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

// IndexItem.java (하단 가변 리스트 항목)
@Entity
@Getter
@Setter
public class IndexItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String no;                // 01, 02...
    private String subject;           // MAIN EXHIBITION
    private String subSubject;        // 빛과 그림자의 변주곡
    private String linkUrl;           // 이동 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_page_id")
    private IndexPage indexPage;
}

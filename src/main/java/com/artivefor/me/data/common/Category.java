package com.artivefor.me.data.common;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type; // INSIGHT or LOG

    @Column(nullable = false)
    private String name; // 화면에 보여줄 이름 (예: Essay, Process)

    @Column(nullable = false, unique = true)
    private String code; // 내부 관리용 코드 (예: essay, process) - 소문자 권장
    
    private int displayOrder; // 정렬 순서
}
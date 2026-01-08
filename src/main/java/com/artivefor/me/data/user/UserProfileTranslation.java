package com.artivefor.me.data.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserProfileTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;     // 해당 언어로 된 작가 이름

    @Column(columnDefinition = "TEXT") // 일반 문자열보다 훨씬 긴 텍스트를 저장할 때 사용
    private String bio;      // 자기소개

    private boolean isAutomated = false; // AI(DeepL 등)가 번역한 것인지 여부 (자동번역 플래그)

    // 특정 언어의 이름과 소개글을 수정하는 메서드
    public void updateContent(String name, String bio) {
        this.name = name;
        this.bio = bio;
        this.isAutomated = false; // 수동으로 수정했으므로 자동번역 플래그 해제
    }
}


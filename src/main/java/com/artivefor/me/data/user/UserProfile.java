package com.artivefor.me.data.user;

import com.artivefor.me.data.common.LanguageCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId // ArtiveUser의 ID를 이 엔티티의 ID로 그대로 사용 (1:1 공유 PK)
    @JoinColumn(name = "user_id") // DB 외래키 이름 설정
    private ArtiveUser user;

    private String thumbnailUrl; // 프로필 이미지 (언어와 무관)

    /**
     * 다국어 번역 데이터를 담는 맵입니다.
     * Key: 언어코드 (예: "ko", "en", "jp")
     * Value: 해당 언어의 번역 객체 (UserProfileTranslation)
     */
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    @MapKeyColumn(name = "lang_code")
    @Enumerated(EnumType.STRING) // 중요: Enum의 이름(KO, EN)을 DB에 저장합니다.
    private Map<LanguageCode, UserProfileTranslation> translations = new HashMap<>();

    // 데이터 추가 시 편리하게 사용할 도우미 메서드
    public void addTranslation(LanguageCode lang, UserProfileTranslation translation) {
        this.translations.put(lang, translation);
    }

    // 프로필 이미지(썸네일)만 수정하는 메서드
    public void updateThumbnail(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


}

/*
* // 1. 유저 프로필 생성
UserProfile profile = new UserProfile();

// 2. 한국어 정보 생성 및 추가
UserProfileTranslation koInfo = new UserProfileTranslation("홍길동", "의적", "안녕하세요.");
profile.addTranslation(LanguageCode.KO, koInfo);

// 3. 영어 정보 생성 및 추가 (자동 번역되었다고 가정)
UserProfileTranslation enInfo = new UserProfileTranslation("Gildong Hong", "Hero", "Hello.");
enInfo.setAutomated(true); // "이건 AI가 번역한 거야" 표시
profile.addTranslation(LanguageCode.EN, enInfo);
*
* */
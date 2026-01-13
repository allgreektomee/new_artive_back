package com.artivefor.me;

import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.user.*;
import com.artivefor.me.repository.user.ArtiveUserRepository;
import com.artivefor.me.security.config.SecurityConfig; // ✅ 직접 추가
import com.artivefor.me.security.jwt.JwtTokenProvider; // ✅ 직접 추가
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({SecurityConfig.class, JwtTokenProvider.class}) // ✅ JwtTokenProvider도 함께 임포트
@ActiveProfiles("test")
class UserProfileRepositoryTest {

    @Autowired
    private ArtiveUserRepository userRepository;

    @Test
    @DisplayName("빌더를 사용하여 유저, 프로필, 번역 데이터를 한꺼번에 저장한다")
    void saveUserFullTest() {
        // 1. 프로필 생성 (빌더 사용)
        UserProfile profile = UserProfile.builder()
                .thumbnailUrl("http://artive.com/thumb.png")
                .build();

        // 2. 번역 데이터 추가
        profile.addTranslation(LanguageCode.KO, UserProfileTranslation.builder()
                .name("관리자")
                .bio("반갑습니다.")
                .build());

        // 3. 유저 생성 (빌더에 프로필 포함)
        ArtiveUser user = ArtiveUser.builder()
                .email("test@artive.com")
                .password("encoded_password")
                .slug("test-admin")
                .role(Role.ADMIN)
                .profile(profile) // ✅ 빌더에서 세팅
                .build();

        // 4. 양방향 관계 맺기 (MapsId 구조상 필수)
        profile.setUser(user);

        // 5. 저장 (ArtiveUser에 cascade = CascadeType.ALL이 있으므로 자식도 같이 저장됨)
        userRepository.save(user);
        userRepository.flush();

        // 6. 검증
        ArtiveUser savedUser = userRepository.findByEmail("test@artive.com").orElseThrow();

        assertThat(savedUser.getProfile()).isNotNull();
        assertThat(savedUser.getProfile().getTranslations().get(LanguageCode.KO).getName())
                .isEqualTo("관리자");
    }
}
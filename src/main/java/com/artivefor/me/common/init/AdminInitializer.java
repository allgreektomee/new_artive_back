package com.artivefor.me.common.init;

import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.user.*;
import com.artivefor.me.repository.user.ArtiveUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final ArtiveUserRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Railway 환경 변수에서 값을 가져옵니다.
    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        // 이메일로 중복 체크
        if (memberRepository.findByEmail(adminEmail).isEmpty()) {

            // 1. 유저 기본 정보 생성
            ArtiveUser admin = ArtiveUser.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .slug("admin-" + UUID.randomUUID().toString().substring(0, 8))
                    .role(Role.ADMIN)
                    .build();

            // 2. 유저 설정(Settings) 연결
            UserSettings settings = UserSettings.builder()
                    .user(admin)
                    .preferredLanguage(LanguageCode.KO)
                    .build();
            admin.setSettings(settings);

            // 3. 유저 프로필(Profile) 및 다국어 초기화
            UserProfile profile = UserProfile.builder()
                    .user(admin)
                    .thumbnailUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=Admin")
                    .build();

            // 한글 이름 설정
            profile.addTranslation(LanguageCode.KO, UserProfileTranslation.builder()
                    .name("관리자")
                    .bio("시스템 관리자입니다.")
                    .build());

            // 영어 이름 설정
            profile.addTranslation(LanguageCode.EN, UserProfileTranslation.builder()
                    .name("Administrator")
                    .bio("System administrator.")
                    .build());

            admin.setProfile(profile);

            // 4. 저장 (CascadeType.ALL로 인해 하위 엔티티까지 모두 저장됨)
            memberRepository.save(admin);

            System.out.println("=== ✅ Admin account created successfully from environment variables ===");
        }
    }
}
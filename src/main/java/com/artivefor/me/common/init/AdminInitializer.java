package com.artivefor.me.common.init;

import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.common.SystemConfig;
import com.artivefor.me.data.common.UpdateStatus;
import com.artivefor.me.data.user.*;
import com.artivefor.me.repository.user.ArtiveUserRepository;
import com.artivefor.me.repository.common.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final ArtiveUserRepository memberRepository;
    private final SystemConfigRepository systemConfigRepository; // 추가
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        // 1. 관리자 계정 초기화 (기존 로직)
        initAdminAccount();

        // 2. 시스템 설정 초기화 (추가)
        initSystemConfig();
    }

    private void initAdminAccount() {
        if (memberRepository.findByEmail(adminEmail).isEmpty()) {
            ArtiveUser admin = ArtiveUser.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .slug("admin-" + UUID.randomUUID().toString().substring(0, 8))
                    .role(Role.ADMIN)
                    .build();

            // ... (기존 Settings, Profile 설정 로직 동일)

            memberRepository.save(admin);
            System.out.println("=== ✅ Admin account created successfully ===");
        }
    }

    private void initSystemConfig() {
        // ID 1번이 있는지 확인하고 없으면 생성
        if (systemConfigRepository.findById(1L).isEmpty()) {
            SystemConfig defaultConfig = SystemConfig.builder()
                    .isMaintenance(false)
                    .maintenanceMessage("현재 시스템 점검 중입니다.")
                    .iosUpdateStatus(UpdateStatus.NORMAL)
                    .iosMinVersion("1.0.0")
                    .iosLatestVersion("1.0.0")
                    .aosUpdateStatus(UpdateStatus.NORMAL)
                    .aosMinVersion("1.0.0")
                    .aosLatestVersion("1.0.0")
                    .isNoticeActive(false)
                    .noticeTitle("새로운 소식")
                    .noticeContent("앱이 성공적으로 실행되었습니다.")
                    .build();

            systemConfigRepository.save(defaultConfig);
            System.out.println("=== ✅ Initial System Config created (ID: 1) ===");
        }
    }
}
package com.artivefor.me.service.user;

import com.artivefor.me.common.exception.BusinessException;
import com.artivefor.me.common.util.MessageCode;
import com.artivefor.me.common.util.MessageUtil;
import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.user.*;
import com.artivefor.me.dto.user.ProfileDto;
import com.artivefor.me.dto.user.SignupRequest;
import com.artivefor.me.repository.user.ArtiveUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class UserService {

    private final ArtiveUserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 주입 필요

    @Transactional
    public void updateProfile(String email, ProfileDto.UpdateRequest request) {
        ArtiveUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(MessageUtil.getMessage(MessageCode.USER_NOT_FOUND)));

        UserProfile profile = user.getProfile();

        // 1. 공통 정보 수정 (썸네일)
        if (request.getThumbnailUrl() != null) {
            profile.updateThumbnail(request.getThumbnailUrl());
        }

        // 2. 다국어 정보 수정
        // UserProfileTranslation에 @AllArgsConstructor나 @Builder가 있으므로 이를 활용
        UserProfileTranslation translation = profile.getTranslations()
                .computeIfAbsent(request.getLanguage(), lang -> UserProfileTranslation.builder()
                        .isAutomated(false)
                        .build());

        // 내용 업데이트 (이름, 소개글)
        translation.updateContent(request.getName(), request.getBio());
    }

    @Transactional(readOnly = true)
    public ProfileDto.Response getProfile(String email) {
        ArtiveUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(MessageUtil.getMessage(MessageCode.USER_NOT_FOUND)));

        UserProfile profile = user.getProfile();

        Map<LanguageCode, ProfileDto.TranslationResponse> translations = profile.getTranslations().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> ProfileDto.TranslationResponse.builder()
                                .name(e.getValue().getName())
                                .bio(e.getValue().getBio())
                                .isAutomated(e.getValue().isAutomated())
                                .build()
                ));

        return ProfileDto.Response.builder()
                .email(user.getEmail())
                .nickname(user.getSlug()) // slug를 작가명(닉네임)으로 활용
                .thumbnailUrl(profile.getThumbnailUrl())
                .translations(translations)
                .build();
    }

    @Transactional
    public void signup(SignupRequest dto) {
        // 1. 비밀번호 일치 확인
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            throw new BusinessException(MessageCode.USER_PASSWORD_MISMATCH);
        }

        // 2. 이메일 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(MessageCode.USER_ALREADY_REG);
        }

        // 3. 초기 Slug 생성 (이메일 아이디 부분)
        String slug = dto.getEmail().split("@")[0];
        if (userRepository.existsBySlug(slug)) {
            slug = slug + "-" + java.util.UUID.randomUUID().toString().substring(0, 5);
        }

        // 4. ArtiveUser 빌드
        ArtiveUser user = ArtiveUser.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .slug(slug)
                .role(Role.USER)
                .build();

        // 5. UserProfile 초기화 (1:1 공유 PK)
        UserProfile profile = UserProfile.builder()
                .user(user)
                .thumbnailUrl(null) // 나중에 수정 가능
                .build();
        user.setProfile(profile);

        // 6. UserSettings 초기화
        UserSettings settings = UserSettings.builder()
                .user(user)
                .preferredLanguage(LanguageCode.KO) // 기본 언어 한국어 설정
                .build();
        user.setSettings(settings);

        // CascadeType.ALL 설정 덕분에 user만 저장해도 profile, settings가 함께 저장됩니다.
        userRepository.save(user);
    }
}
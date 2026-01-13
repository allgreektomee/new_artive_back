package com.artivefor.me.service.user;

import com.artivefor.me.common.util.MessageCode;
import com.artivefor.me.common.util.MessageUtil;
import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.user.*;
import com.artivefor.me.dto.user.ProfileDto;
import com.artivefor.me.repository.user.ArtiveUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ArtiveUserRepository userRepository;

    @Transactional
    public void updateProfile(String email, ProfileDto.UpdateRequest request) {
        ArtiveUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(MessageUtil.getMessage(MessageCode.USER_NOT_FOUND)));

        UserProfile profile = user.getProfile();

        // 1. 공통 정보 수정 (썸네일 등)
        if (request.getThumbnailUrl() != null) {
            profile.updateThumbnail(request.getThumbnailUrl());
        }

        // 2. 다국어 정보 수정 (해당 언어 번역본이 없으면 생성)
        UserProfileTranslation translation = profile.getTranslations()
                .computeIfAbsent(request.getLanguage(), lang -> new UserProfileTranslation());

        // 내용 업데이트 (이름, 소개글)
        translation.updateContent(request.getName(), request.getBio());
    }

    @Transactional(readOnly = true)
    public ProfileDto.Response getProfile(String email) {
        ArtiveUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(MessageUtil.getMessage(MessageCode.USER_NOT_FOUND)));

        UserProfile profile = user.getProfile();

        // Map<LanguageCode, Translation>을 Map<LanguageCode, TranslationResponse>로 변환
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
                .nickname(user.getSlug())
                .thumbnailUrl(profile.getThumbnailUrl())
                .translations(translations)
                .build();
    }
}
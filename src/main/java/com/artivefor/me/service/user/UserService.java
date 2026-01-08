package com.artivefor.me.service.user;

import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.user.*;
import com.artivefor.me.dto.user.ProfileDto;
import com.artivefor.me.repository.user.ArtiveUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ArtiveUserRepository userRepository;

    @Transactional
    public void updateProfile(String email, ProfileDto.UpdateRequest request) {
        ArtiveUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

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
}
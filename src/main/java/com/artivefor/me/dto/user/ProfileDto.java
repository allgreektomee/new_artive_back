package com.artivefor.me.dto.user;

import com.artivefor.me.data.common.LanguageCode;
import lombok.*;

import java.util.Map;

public class ProfileDto {

    // 프로필 수정 요청용
    @Getter @Setter
    public static class UpdateRequest {
        private String name;
        private String bio;
        private String thumbnailUrl;
        private LanguageCode language; // 수정할 언어 (KO, EN 등)
    }

    // 프로필 응답용
    @Getter @Builder
    public static class Response {
        private String email;
        private String nickname;
        private String thumbnailUrl;
        private Map<LanguageCode, TranslationResponse> translations;
    }

    @Getter @Builder
    public static class TranslationResponse {
        private String name;
        private String bio;
        private boolean isAutomated;
    }
}
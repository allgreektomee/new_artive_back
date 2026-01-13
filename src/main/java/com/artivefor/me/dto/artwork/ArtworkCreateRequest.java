package com.artivefor.me.dto.artwork;

import com.artivefor.me.data.artwork.ArtworkTranslation;
import com.artivefor.me.data.artwork.WorkStatus;
import com.artivefor.me.data.common.LanguageCode;

import java.util.List;
import java.util.Map;

public record ArtworkCreateRequest(
        String startedAt,   // "2026-01-01" 형식
        String finishedAt,  // 완성되지 않았다면 null 가능
        WorkStatus status,
        String medium,
        String size,
        String thumbnailUrl, // ⭐️ 추가: S3에서 받아온 이미지 URL
        Map<LanguageCode, TranslationRequest> translations // Translation도 DTO로 받는 게 깔끔함
) {
    // 내부 클래스로 번역 정보 DTO 정의
    public record TranslationRequest(
            String title,
            String description
    ) {}
}
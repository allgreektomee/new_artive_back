package com.artivefor.me.dto.artwork;

import com.artivefor.me.data.artwork.ArtworkHistoryTranslation;
import com.artivefor.me.data.artwork.HistoryType;
import com.artivefor.me.data.common.LanguageCode;

import java.util.Map;

public record HistoryCreateRequest(
        String imageUrl,      // S3 업로드 후 받은 URL
        HistoryType type,     // MANUAL, YOUTUBE 등
        Map<LanguageCode, HistoryTranslationRequest> translations
) {
    public record HistoryTranslationRequest(
            String title,       // 히스토리 제목
            String description  // 히스토리 상세 설명
    ) {}
}
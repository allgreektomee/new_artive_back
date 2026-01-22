package com.artivefor.me.dto.artwork;


import com.artivefor.me.data.artwork.HistoryType;
import com.artivefor.me.data.common.Visibility;


public record HistoryCreateRequest(
        String imageUrl,
        HistoryType type,
        Visibility visibility,
        // 평면화된 다국어 필드
        String koTitle,
        String koContent,
        String enTitle,
        String enContent
) {}
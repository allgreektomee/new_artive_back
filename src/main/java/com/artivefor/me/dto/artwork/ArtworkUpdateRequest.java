package com.artivefor.me.dto.artwork;

import com.artivefor.me.data.artwork.ArtworkTranslation;
import com.artivefor.me.data.artwork.WorkStatus;
import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.common.Visibility;

import java.util.List;
import java.util.Map;

public record ArtworkUpdateRequest(
        String koTitle,
        String koDescription,
        String enTitle,
        String enDescription,
        String thumbnailUrl,
        List<String> images,
        Visibility visibility,
        String medium,
        String size,
        String startedAt,
        String finishedAt,
        WorkStatus status
) {}
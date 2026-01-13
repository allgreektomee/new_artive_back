package com.artivefor.me.dto.artwork;

import com.artivefor.me.data.artwork.ArtworkTranslation;
import com.artivefor.me.data.artwork.WorkStatus;
import com.artivefor.me.data.common.LanguageCode;

import java.util.Map;

public record ArtworkUpdateRequest(
        WorkStatus status,
        String medium,
        String size,
        Map<LanguageCode, ArtworkTranslation> translations
) {}
package com.artivefor.me.dto.artwork;

import com.artivefor.me.data.artwork.ArtworkHistoryTranslation;
import com.artivefor.me.data.common.LanguageCode;

import java.util.Map;

public record HistoryUpdateRequest(
        String imageUrl,
        Map<LanguageCode, ArtworkHistoryTranslation> translations
) {}
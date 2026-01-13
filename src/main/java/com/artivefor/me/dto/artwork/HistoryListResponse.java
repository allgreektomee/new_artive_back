package com.artivefor.me.dto.artwork;

import com.artivefor.me.data.artwork.HistoryType;

import java.time.LocalDateTime;

public record HistoryListResponse(
        Long id,
        String imageUrl,
        HistoryType type,
        String description,
        LocalDateTime createdAt
) {}
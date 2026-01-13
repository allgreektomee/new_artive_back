package com.artivefor.me.dto.artwork;

import com.artivefor.me.data.artwork.WorkStatus;
import lombok.Builder;

@Builder
public record ArtworkListResponse(
        Long id,
        String thumbnailUrl,
        String title,      // 현재 언어에 맞는 제목
        WorkStatus status,
        long totalHistoryCount // (선택) 히스토리가 몇 개 달렸는지 보여주면 좋음
) {}
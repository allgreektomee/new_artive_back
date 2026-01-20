package com.artivefor.me.dto.artwork;

import com.artivefor.me.data.artwork.WorkStatus;
import com.artivefor.me.data.common.Visibility;
import lombok.*;

import java.util.List;

@Builder
public record ArtworkDetailResponse(
    Long id,
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
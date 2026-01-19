package com.artivefor.me.dto.artwork;

import com.artivefor.me.data.common.Visibility;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtworkDetailResponse {
    private Long id;
    private String thumbnailUrl;
    private String medium;
    private String size;
    private Visibility visibility; // PUBLIC, PRIVATE

    // 프론트엔드 ArtworkFormValues와 매칭되는 필드
    private String koTitle;
    private String koDescription;
    private String enTitle;
    private String enDescription;

    // 필요한 경우 날짜 필드 추가
    private String startedAt;
    private String finishedAt;
}
package com.artivefor.me.dto.index;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class IndexPageResponseDto {
    private HeaderDto header;           // 상단 디자인 & 워딩
    private ContentsConfigDto config;   // 목차 공통 디자인 설정
    private List<ItemDto> items;        // 실제 목차 리스트

    @Getter @Builder
    public static class HeaderDto {
        private String issueNo;
        private String bgColor;
        private String titleTag;
        private String titleTagColor;
        private String mainTitle;
        private String mainTitleColor;
        private String description;
        private String descriptionColor;
    }

    @Getter @Builder
    public static class ContentsConfigDto {
        private String tag;
        private String tagColor;
        private String noColor;
        private String titleColor;
        private String subColor;
    }

    @Getter @Builder
    public static class ItemDto {
        private String no;
        private String subject;
        private String subSubject;
        private String linkUrl;
    }
}
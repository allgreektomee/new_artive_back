package com.artivefor.me.dto.index;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class IndexPageRequestDTO {
    // 상단 및 전체 설정
    private String issueNo;
    private String backgroundColor;

    // 헤더(프롤로그) 커스텀
    private String titleTag;
    private String titleTagColor;
    private String mainTitle;
    private String mainTitleColor;
    private String description;
    private String descriptionColor;

    // 목차 섹션 커스텀
    private String contentsTag;
    private String contentsTagColor;
    private String contentsNoColor;
    private String contentsTitleColor;
    private String contentsSubColor;

    // 가변 리스트 항목들
    private List<ItemRequest> items;

    @Getter @Setter
    public static class ItemRequest {
        private String no;
        private String subject;
        private String subSubject;
        private String linkUrl;
    }
}
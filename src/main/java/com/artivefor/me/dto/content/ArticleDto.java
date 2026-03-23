package com.artivefor.me.dto.content;

import lombok.*;

/**
 * 인사이트(Insight) 및 로그(Log) 기사 생성을 위한 DTO
 * 컨트롤러의 createArticle, updateArticle 메서드에서 @RequestBody로 사용됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticleDto {

    // 1. 카테고리 식별자 (컨트롤러에서 categoryRepository.findById(request.categoryId)로 사용)
    private Long categoryId;

    // 2. 제목 (국문/영문)
    private String koTitle;
    private String enTitle;

    // 3. 목록용 정보 및 연결 링크
    private String summary;      // 목록에 노출되는 짧은 설명
    private String externalUrl;  // 상세페이지로 갈 외부 페이지 링크
    private String thumbnailUrl; // S3 이미지 주소

    // 4. 공개 여부 (기본값 true)
    @Builder.Default
    private Boolean isPublic = true;
}
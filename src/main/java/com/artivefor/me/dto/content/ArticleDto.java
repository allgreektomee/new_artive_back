package com.artivefor.me.dto.content;

import lombok.*;

/**
 * 인사이트(Insight)와 로그(Log) 통합 관리를 위한 DTO
 * 컨트롤러의 createArticle, updateArticle 메서드에서 @RequestBody로 사용됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticleDto {

    // 1. 카테고리 연결 (컨트롤러에서 categoryRepository.findById(request.categoryId)로 사용)
    private Long categoryId;

    // 2. 제목 (국문/영문)
    private String koTitle;
    private String enTitle;

    // 3. 내용 요약 및 본문 연결
    private String summary;      // 목록에 노출되는 짧은 설명
    private String externalUrl;  // 워드프레스 등 외부 콘텐츠 링크
    private String thumbnailUrl; // S3 이미지 주소

    // 4. 상태 관리 (필요 시 추가)
    @Builder.Default
    private Boolean isPublic = true;
}
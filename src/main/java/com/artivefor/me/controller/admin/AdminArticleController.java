package com.artivefor.me.controller.admin;

import com.artivefor.me.common.util.MessageCode;
import com.artivefor.me.data.common.Category;
import com.artivefor.me.data.common.CategoryType;
import com.artivefor.me.data.content.Article;
import com.artivefor.me.data.content.ContentType;
import com.artivefor.me.dto.common.ApiResponse;
import com.artivefor.me.repository.content.CategoryRepository;
import com.artivefor.me.repository.content.ArticleRepository;
import com.artivefor.me.dto.content.ArticleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminArticleController {


    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;

    // === 통합 저장 로직 ===
    @PostMapping("/{resource}") // /admin/insights 또는 /admin/logs
    public ApiResponse<Long> createArticle(@PathVariable String resource, @RequestBody ArticleDto request) {
        ContentType contentType = ContentType.valueOf(resource.toUpperCase().substring(0, resource.length() - 1)); // insights -> INSIGHT
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID"));

        Article article = Article.builder()
                .type(contentType)
                .category(category)
                .koTitle(request.getKoTitle())
                .enTitle(request.getEnTitle())
                .summary(request.getSummary())
                .externalUrl(request.getExternalUrl())
                .thumbnailUrl(request.getThumbnailUrl())
                .isPublic(true)
                .build();

        Article saved = articleRepository.save(article);
        return ApiResponse.success(saved.getId(), MessageCode.SUCCESS);
    }

    @GetMapping("/{resource}/categories")
    public ApiResponse<List<Category>> getCategories(@PathVariable String resource) {
        // 1. URL의 resource(insights/logs)를 CategoryType(INSIGHT/LOG)으로 변환
        // substring 로직을 그대로 쓰거나, 명시적으로 매핑
        CategoryType type = CategoryType.valueOf(resource.toUpperCase().substring(0, resource.length() - 1));

        // 2. 레포지토리에서 해당 타입의 카테고리만 순서대로 가져옴
        List<Category> categories = categoryRepository.findAllByTypeOrderByDisplayOrderAsc(type);

        return ApiResponse.success(categories, MessageCode.SUCCESS);
    }

    // === 통합 조회 로직 ===
    @GetMapping("/{resource}")
    public ApiResponse<Page<Article>> getArticles(@PathVariable String resource, @PageableDefault(size = 10) Pageable pageable) {
        // URL 경로(insights/logs)를 Enum으로 변환 (단수형으로)
        ContentType contentType = ContentType.valueOf(resource.toUpperCase().substring(0, resource.length() - 1));
        return ApiResponse.success(articleRepository.findAllByType(contentType, pageable), MessageCode.SUCCESS);
    }

    // === 상세 조회 ===
    @GetMapping("/{resource}/{id}")
    public ApiResponse<Article> getArticle(@PathVariable String resource, @PathVariable Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found"));
        return ApiResponse.success(article, MessageCode.SUCCESS);
    }

    @PutMapping("/{resource}/{id}")
    @Transactional // 데이터 수정을 위해 트랜잭션 추가 필수!
    public ApiResponse<Long> updateArticle(@PathVariable String resource, @PathVariable Long id, @RequestBody ArticleDto request) {
        // 1. 기존 게시글 조회
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2. 카테고리 변경이 있을 경우 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID"));

        // 3. 엔티티의 update 메서드 호출 (Dirty Checking으로 자동 저장)
        article.update(
                category,
                request.getKoTitle(),
                request.getEnTitle(),
                request.getSummary(),
                request.getExternalUrl(),
                request.getThumbnailUrl()
        );

        return ApiResponse.success(id, MessageCode.SUCCESS);
    }

    // === 삭제 ===
    @DeleteMapping("/{resource}/{id}")
    public ApiResponse<Void> deleteArticle(@PathVariable String resource, @PathVariable Long id) {
        articleRepository.deleteById(id);
        return ApiResponse.success(MessageCode.SUCCESS);
    }
    
    // 삭제 등 추가 구현 가능

}
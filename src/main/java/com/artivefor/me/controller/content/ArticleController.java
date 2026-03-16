package com.artivefor.me.controller.content;

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
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;

    // === 목록 조회: GET /api/v1/articles?type=INSIGHT ===
    // === 목록 조회 (타입별 전체 또는 카테고리별 선택 조회) ===
    @GetMapping
    public ApiResponse<Page<Article>> getArticles(
            @RequestParam ContentType type,
            @RequestParam(required = false) Long categoryId, // 💡 카테고리 ID 추가
            @PageableDefault(size = 10) Pageable pageable) {

        if (categoryId != null) {
            // 특정 카테고리의 글만 조회
            return ApiResponse.success(
                    articleRepository.findAllByTypeAndCategoryId(type, categoryId, pageable),
                    MessageCode.SUCCESS
            );
        }

        // 해당 타입(INSIGHT/LOG) 전체 조회
        return ApiResponse.success(
                articleRepository.findAllByType(type, pageable),
                MessageCode.SUCCESS
        );
    }

    // === 카테고리 조회: GET /api/v1/articles/categories?type=INSIGHT ===
    @GetMapping("/categories")
    public ApiResponse<List<Category>> getCategories(@RequestParam CategoryType type) {
        return ApiResponse.success(categoryRepository.findAllByTypeOrderByDisplayOrderAsc(type), MessageCode.SUCCESS);
    }

    // === 저장: POST /api/v1/articles?type=INSIGHT ===
    @PostMapping
    public ApiResponse<Long> createArticle(@RequestParam ContentType type, @RequestBody ArticleDto request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID"));

        Article article = Article.builder()
                .type(type)
                .category(category)
                .koTitle(request.getKoTitle())
                .enTitle(request.getEnTitle())
                .summary(request.getSummary())
                .externalUrl(request.getExternalUrl())
                .thumbnailUrl(request.getThumbnailUrl())
                .isPublic(true)
                .build();

        return ApiResponse.success(articleRepository.save(article).getId(), MessageCode.SUCCESS);
    }

    // === 수정/삭제: /api/v1/articles/{id} ===
    @PutMapping("/{id}")
    @Transactional
    public ApiResponse<Long> updateArticle(@PathVariable Long id, @RequestBody ArticleDto request) {
        Article article = articleRepository.findById(id).orElseThrow();
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();
        article.update(category, request.getKoTitle(), request.getEnTitle(), request.getSummary(), request.getExternalUrl(), request.getThumbnailUrl());
        return ApiResponse.success(id, MessageCode.SUCCESS);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteArticle(@PathVariable Long id) {
        articleRepository.deleteById(id);
        return ApiResponse.success(MessageCode.SUCCESS);
    }
}
package com.artivefor.me.controller.index;

import com.artivefor.me.dto.index.IndexPageRequestDTO;
import com.artivefor.me.dto.index.IndexPageResponseDTO;
import com.artivefor.me.service.index.IndexPageService;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/index-page")
@RequiredArgsConstructor
public class IndexPageController {

    private final IndexPageService indexPageService;

    /**
     * 1. 최신 인덱스 페이지 설정 조회 (App/Web 메인용)
     */
    @GetMapping("/latest")
    public ResponseEntity<IndexPageResponseDTO> getLatestIndexPage() {
        return ResponseEntity.ok(indexPageService.getLatestIndexPage());
    }

    // 2. 전체 목록 페이징 조회 (Get 방식)
    // 예: /api/v1/index?page=0&size=10
    @GetMapping
    public ResponseEntity<Page<IndexPageResponseDTO>> getAllIndexPages(
            @PageableDefault(size = 10, sort = "issueNo", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(indexPageService.getAllIndexPages(pageable));
    }

    // 3. 특정 issueNo로 조회
    // 예: /api/v1/index/issue/2026-01
    @GetMapping("/issue/{issueNo}")
    public ResponseEntity<IndexPageResponseDTO> getIndexPageByIssueNo(@PathVariable String issueNo) {
        return ResponseEntity.ok(indexPageService.getIndexPageByIssueNo(issueNo));
    }
    /**
     * 2. 인덱스 페이지 설정 저장 및 업데이트 (Admin용)
     */
    @PostMapping("/save")
    public ResponseEntity<Long> saveIndexPage(@RequestBody IndexPageRequestDTO requestDto) {
        Long id = indexPageService.saveIndexPage(requestDto);
        return ResponseEntity.ok(id);
    }
}
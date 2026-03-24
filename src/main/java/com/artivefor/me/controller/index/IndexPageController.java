package com.artivefor.me.controller.index;

import com.artivefor.me.dto.index.IndexPageRequestDTO;
import com.artivefor.me.dto.index.IndexPageResponseDTO;
import com.artivefor.me.service.index.IndexPageService;
import lombok.RequiredArgsConstructor;
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

    /**
     * 2. 인덱스 페이지 설정 저장 및 업데이트 (Admin용)
     */
    @PostMapping("/save")
    public ResponseEntity<Long> saveIndexPage(@RequestBody IndexPageRequestDTO requestDto) {
        Long id = indexPageService.saveIndexPage(requestDto);
        return ResponseEntity.ok(id);
    }
}
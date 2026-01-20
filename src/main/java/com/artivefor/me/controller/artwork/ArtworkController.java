package com.artivefor.me.controller.artwork;

import com.artivefor.me.data.common.ArtworkConstants;
import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.dto.artwork.*;
import com.artivefor.me.dto.common.ApiResponse;
import com.artivefor.me.dto.image.ImageUploadResponse;
import com.artivefor.me.service.artwork.ArtworkHistoryService;
import com.artivefor.me.service.artwork.ArtworkService;
import com.artivefor.me.service.image.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import com.artivefor.me.common.util.MessageCode;
@RestController
@RequestMapping("/api/v1/artworks")
@RequiredArgsConstructor
public class ArtworkController {

    private final ArtworkService artworkService;
    private final ArtworkHistoryService artworkHistoryService;


    // --- [작품 본체 관련] ---

    @PostMapping
    public ApiResponse<Long> createArtwork(
            @AuthenticationPrincipal ArtiveUser user,
            @RequestBody ArtworkCreateRequest request // 내부에 S3에서 받은 thumbnailUrl 포함됨
    ) {
        return ApiResponse.success(artworkService.createArtwork(user.getId(), request), MessageCode.SUCCESS);
    }

    /**
     * 작품 상세 조회 (수정 폼 세팅용)
     */
    @GetMapping("/{id}")
    public ApiResponse<ArtworkDetailResponse> getArtwork(@PathVariable Long id) {
        // Service에서 해당 ID의 작품을 찾아 DTO로 변환해 반환
        return ApiResponse.success(artworkService.getArtworkDetail(id), MessageCode.SUCCESS);
    }

    @PutMapping("/{artworkId}")
    public ApiResponse<Void> updateArtwork(
            @AuthenticationPrincipal ArtiveUser user,
            @PathVariable Long artworkId,
            @RequestBody ArtworkUpdateRequest request
    ) {
        artworkService.updateArtwork( artworkId, request);
        return ApiResponse.success(MessageCode.SUCCESS);
    }

    @DeleteMapping("/{artworkId}")
    public ApiResponse<Void> deleteArtwork(
            @AuthenticationPrincipal ArtiveUser user,
            @PathVariable Long artworkId
    ) {
        artworkService.deleteArtwork(user.getId(), artworkId);
        return ApiResponse.success(MessageCode.SUCCESS);
    }

    @GetMapping
    public ApiResponse<Page<ArtworkListResponse>> getMyArtworks(
            @AuthenticationPrincipal ArtiveUser user,
            @RequestParam(defaultValue = ArtworkConstants.DEFAULT_PAGE_NUMBER) int page
    ) {
        return ApiResponse.success(artworkService.getMyArtworks(user.getId(), page), MessageCode.SUCCESS);
    }


    // --- [히스토리 관련] ---

    @PostMapping("/{artworkId}/histories")
    public ApiResponse<Long> addHistory(
            @AuthenticationPrincipal ArtiveUser user,
            @PathVariable Long artworkId,
            @RequestBody HistoryCreateRequest request
    ) {
        return ApiResponse.success(artworkHistoryService.createHistory(user.getId(), artworkId, request), MessageCode.SUCCESS);
    }

    @DeleteMapping("/histories/{historyId}")
    public ApiResponse<Void> deleteHistory(
            @AuthenticationPrincipal ArtiveUser user,
            @PathVariable Long historyId
    ) {
        artworkHistoryService.deleteHistory(user.getId(), historyId);
        return ApiResponse.success(MessageCode.SUCCESS);
    }

    /**
     * 2. 특정 작품의 히스토리 목록만 조회
     */
    @GetMapping("/{artworkId}/histories")
    public ApiResponse<List<HistoryListResponse>> getArtworkHistories(
            @PathVariable Long artworkId,
            @RequestParam(defaultValue = "KO") LanguageCode lang,
            @RequestParam(defaultValue = ArtworkConstants.DEFAULT_PAGE_NUMBER) int page)
    {
        return ApiResponse.success(artworkHistoryService.getHistoryList(artworkId, lang, page), MessageCode.SUCCESS);
    }
}
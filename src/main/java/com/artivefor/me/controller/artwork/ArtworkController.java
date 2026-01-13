package com.artivefor.me.controller.artwork;

import com.artivefor.me.data.common.ArtworkConstants;
import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.dto.artwork.*;
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

@RestController
@RequestMapping("/api/v1/artworks")
@RequiredArgsConstructor
public class ArtworkController {

    private final ArtworkService artworkService;
    private final ArtworkHistoryService artworkHistoryService;


    // --- [작품 본체 관련] ---

    @PostMapping
    public ResponseEntity<Long> createArtwork(
            @AuthenticationPrincipal ArtiveUser user,
            @RequestBody ArtworkCreateRequest request // 내부에 S3에서 받은 thumbnailUrl 포함됨
    ) {
        return ResponseEntity.ok(artworkService.createArtwork(user.getId(), request));
    }

    @PutMapping("/{artworkId}")
    public ResponseEntity<Void> updateArtwork(
            @AuthenticationPrincipal ArtiveUser user,
            @PathVariable Long artworkId,
            @RequestBody ArtworkUpdateRequest request
    ) {
        artworkService.updateArtwork(user.getId(), artworkId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{artworkId}")
    public ResponseEntity<Void> deleteArtwork(
            @AuthenticationPrincipal ArtiveUser user,
            @PathVariable Long artworkId
    ) {
        artworkService.deleteArtwork(user.getId(), artworkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ArtworkListResponse>> getMyArtworks(
            @AuthenticationPrincipal ArtiveUser user,
            @RequestParam(defaultValue = ArtworkConstants.DEFAULT_PAGE_NUMBER) int page
    ) {
        return ResponseEntity.ok(artworkService.getMyArtworks(user.getId(), page));
    }


    // --- [히스토리 관련] ---

    @PostMapping("/{artworkId}/histories")
    public ResponseEntity<Long> addHistory(
            @AuthenticationPrincipal ArtiveUser user,
            @PathVariable Long artworkId,
            @RequestBody HistoryCreateRequest request
    ) {
        return ResponseEntity.ok(artworkHistoryService.createHistory(user.getId(), artworkId, request));
    }

    @DeleteMapping("/histories/{historyId}")
    public ResponseEntity<Void> deleteHistory(
            @AuthenticationPrincipal ArtiveUser user,
            @PathVariable Long historyId
    ) {
        artworkHistoryService.deleteHistory(user.getId(), historyId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 2. 특정 작품의 히스토리 목록만 조회
     */
    @GetMapping("/{artworkId}/histories")
    public ResponseEntity<List<HistoryListResponse>> getArtworkHistories(
            @PathVariable Long artworkId,
            @RequestParam(defaultValue = "KO") LanguageCode lang,
            @RequestParam(defaultValue = ArtworkConstants.DEFAULT_PAGE_NUMBER) int page)
    {
        return ResponseEntity.ok(artworkHistoryService.getHistoryList(artworkId, lang, page));
    }
}
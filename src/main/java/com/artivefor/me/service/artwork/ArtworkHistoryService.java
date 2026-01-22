package com.artivefor.me.service.artwork;

import com.artivefor.me.data.artwork.Artwork;
import com.artivefor.me.data.artwork.ArtworkHistory;
import com.artivefor.me.data.artwork.ArtworkHistoryTranslation;
import com.artivefor.me.data.common.ArtworkConstants;
import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.common.Visibility;
import com.artivefor.me.dto.artwork.HistoryCreateRequest;
import com.artivefor.me.dto.artwork.HistoryListResponse;
import com.artivefor.me.repository.artwork.ArtworkHistoryRepository;
import com.artivefor.me.repository.artwork.ArtworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ArtworkHistoryService {

    private final ArtworkRepository artworkRepository;
    private final ArtworkHistoryRepository historyRepository;


    /**
     * 히스토리 추가 (userId 기반)
     */
    @Transactional
    public Long createHistory(Long userId, Long artworkId, HistoryCreateRequest request) {
        // 1. 대상 작품 조회 (작가 정보까지 같이 가져옴)
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작품입니다."));

        // 2. ⭐️ 본인 확인 로직: 작품의 주인과 로그인한 유저가 같은지 체크
        if (!artwork.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("본인의 작품에만 히스토리를 등록할 수 있습니다.");
        }

        // 3. 히스토리 엔티티 생성
        ArtworkHistory history = ArtworkHistory.builder()
                .artwork(artwork)
                .imageUrl(request.imageUrl())
                .type(request.type())
                .visibility(request.visibility() != null ? request.visibility() : Visibility.PUBLIC)
                .build();

        // 3. ⭐️ 다국어 정보 처리 (DTO -> Entity 변환 로직)
        addHistoryTranslation(history, LanguageCode.KO, request.koTitle(), request.koContent());
        addHistoryTranslation(history, LanguageCode.EN, request.enTitle(), request.enContent());

        return historyRepository.save(history).getId();
    }

    /** 🚀 다국어 추가 헬퍼 메서드 */
    private void addHistoryTranslation(ArtworkHistory history, LanguageCode lang, String title, String content) {
        if (title != null && !title.isBlank()) {
            ArtworkHistoryTranslation translation = ArtworkHistoryTranslation.builder()
                    .title(title)
                    .content(content)
                    .build();
            history.addTranslation(lang, translation);
        }
    }

    @Transactional(readOnly = true)
    public Page<HistoryListResponse> getHistoryList(Long artworkId, LanguageCode lang,int page) {
        // 1. 작품이 존재하는지 먼저 확인
        if (!artworkRepository.existsById(artworkId)) {
            throw new IllegalArgumentException("존재하지 않는 작품입니다.");
        }

        PageRequest pageRequest = PageRequest.of(page, ArtworkConstants.PAGE_SIZE);
        Page<ArtworkHistory> historyPage = historyRepository.findByArtworkIdOrderByCreatedAtDesc(artworkId, pageRequest);

        // 🚀 .toList() 대신 .map()을 사용해서 Page 객체를 유지합니다.
        return historyPage.map(history -> {
            ArtworkHistoryTranslation translation = history.getTranslations().get(lang);
            if (translation == null) {
                translation = history.getTranslations().get(LanguageCode.KO);
            }

            return new HistoryListResponse(
                    history.getId(),
                    history.getImageUrl(),
                    history.getType(),
                    translation != null ? translation.getTitle() : "Untitled",
                    translation != null ? translation.getContent() : "",
                    history.getCreatedAt()
            );
        });

    }

    /**
     * 히스토리 삭제 (userId 기반)
     */
    @Transactional
    public void deleteHistory(Long userId, Long historyId) {
        ArtworkHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("히스토리를 찾을 수 없습니다."));

        // [보안] 해당 히스토리가 속한 작품의 작가가 현재 로그인한 유저인지 체크
        if (!history.getArtwork().getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("본인의 작품 기록만 삭제할 수 있습니다.");
        }

        historyRepository.delete(history);
    }
}
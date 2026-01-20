package com.artivefor.me.service.artwork;

import com.artivefor.me.data.artwork.Artwork;
import com.artivefor.me.data.artwork.ArtworkHistory;
import com.artivefor.me.data.artwork.ArtworkTranslation;
import com.artivefor.me.data.common.ArtworkConstants;
import com.artivefor.me.data.common.LanguageCode;
import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.dto.artwork.ArtworkCreateRequest;
import com.artivefor.me.dto.artwork.ArtworkDetailResponse;
import com.artivefor.me.dto.artwork.ArtworkListResponse;
import com.artivefor.me.dto.artwork.ArtworkUpdateRequest;
import com.artivefor.me.repository.artwork.ArtworkHistoryRepository;
import com.artivefor.me.repository.artwork.ArtworkRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true) // 기본적으로 읽기 전용으로 설정 (성능 최적화)
@RequiredArgsConstructor
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final EntityManager entityManager;

    @Transactional // 쓰기 작업에만 별도로 선언
    public Long createArtwork(Long userId, ArtworkCreateRequest request) {
        // getReference를 사용하면 User 테이블을 Select 하지 않고 ID만 가진 가짜 객체를 생성해 FK로 씁니다.
        ArtiveUser author = entityManager.getReference(ArtiveUser.class, userId);

        // 2. 메인 엔티티 생성 (thumbnailUrl 필드 잊지 말고 추가!)
        Artwork artwork = Artwork.builder()
                .author(author)
                .status(request.status())
                .medium(request.medium())
                .size(request.size())
                .thumbnailUrl(request.thumbnailUrl()) // DTO에 추가된 필드 매핑
                .images(request.images())
                .startedAt(parseDate(request.startedAt()))
                .finishedAt(parseDate(request.finishedAt()))
                .build();

        // 3. 다국어 정보 변환 (DTO -> Entity)
        if (request.translations() != null) {
            request.translations().forEach((lang, transDto) -> {
                // transDto는 record TranslationRequest(String title, String description)
                ArtworkTranslation translation = ArtworkTranslation.builder()
                        .title(transDto.title())
                        .description(transDto.description())
                        .build();
                artwork.addTranslation(lang, translation);
            });
        }

        return artworkRepository.save(artwork).getId();
    }

    public ArtworkDetailResponse getArtworkDetail(Long id) {
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작품을 찾을 수 없습니다."));

        // 다국어 정보 추출
        ArtworkTranslation ko = artwork.getTranslations().get(LanguageCode.KO);
        ArtworkTranslation en = artwork.getTranslations().get(LanguageCode.EN);

        return ArtworkDetailResponse.builder()
                .id(artwork.getId())
                .thumbnailUrl(artwork.getThumbnailUrl())
                .images(artwork.getImages())
                .medium(artwork.getMedium())
                .size(artwork.getSize())
                .visibility(artwork.getVisibility())
                // 다국어 세팅 (null 체크 포함)
                .koTitle(ko != null ? ko.getTitle() : "")
                .koDescription(ko != null ? ko.getDescription() : "")
                .enTitle(en != null ? en.getTitle() : "")
                .enDescription(en != null ? en.getDescription() : "")
                .startedAt(artwork.getStartedAt() != null ? artwork.getStartedAt().toString() : null)
                .finishedAt(artwork.getFinishedAt() != null ? artwork.getFinishedAt().toString() : null)
                .build();
    }

    public Page<ArtworkListResponse> getMyArtworks(Long userId, int page) {
        ArtiveUser author = entityManager.getReference(ArtiveUser.class, userId);
        // 하드코딩된 10 대신 상수를 사용
        PageRequest pageRequest = PageRequest.of(page, ArtworkConstants.PAGE_SIZE);

        Page<Artwork> artworkPage = artworkRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageRequest);

        return artworkPage.map(artwork -> {
            ArtworkTranslation translation = artwork.getTranslations().get(LanguageCode.KO);
            String title = (translation != null) ? translation.getTitle() : "Untitled";

            return ArtworkListResponse.builder()
                    .id(artwork.getId())
                    .thumbnailUrl(artwork.getThumbnailUrl())
                    .title(title)
                    .status(artwork.getStatus())
                    .totalHistoryCount(artwork.getHistories().size())
                    .build();
        });
    }

    @Transactional
    public void updateArtwork(Long userId, Long artworkId, ArtworkUpdateRequest request) {
        ArtiveUser author = entityManager.getReference(ArtiveUser.class, userId);
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작품입니다."));

        // [소유권 체크] 작성자와 현재 유저가 다르면 실행 중단
        if (!artwork.getAuthor().getId().equals(userId)) {
            return; // 혹은 throw new RuntimeException("권한 없음");
        }

        artwork.updateInfo(request.status(), request.medium(), request.size());

        if (request.translations() != null) {
            request.translations().forEach(artwork::addTranslation);
        }
    }

    @Transactional
    public void deleteArtwork(Long userId, Long artworkId) {
        ArtiveUser author = entityManager.getReference(ArtiveUser.class, userId);
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작품입니다."));

        // [소유권 체크]
        if (!artwork.getAuthor().getId().equals(userId)) {
            return;
        }

        artworkRepository.deleteById(artworkId);
    }

    // 날짜 파싱 헬퍼 메서드 (중복 제거 및 Null/Empty 체크)
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return LocalDate.parse(dateStr);
    }

}

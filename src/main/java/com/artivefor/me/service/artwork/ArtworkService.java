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
import java.util.ArrayList;
import java.util.Map;

@Service
@Transactional(readOnly = true) // 기본적으로 읽기 전용으로 설정 (성능 최적화)
@RequiredArgsConstructor
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final EntityManager entityManager;

    @Transactional // 쓰기 작업에만 별도로 선언
    public Long createArtwork(Long userId, ArtworkCreateRequest request) {
        ArtiveUser author = entityManager.getReference(ArtiveUser.class, userId);

        // 1. 메인 엔티티 생성 (모든 메타데이터 매핑)
        Artwork artwork = Artwork.builder()
                .author(author)
                .status(request.status())        // 🚀 추가: IN_PROGRESS, COMPLETED 등
                .visibility(request.visibility()) // PUBLIC, PRIVATE
                .medium(request.medium())
                .size(request.size())
                .thumbnailUrl(request.thumbnailUrl())
                .images(new ArrayList<>(request.images())) // @OrderColumn 순서 보존
                .startedAt(parseDate(request.startedAt()))
                .finishedAt(parseDate(request.finishedAt()))
                .build();

        // 2. 다국어 정보 변환 및 추가
        if (request.koTitle() != null) {
            artwork.addTranslation(LanguageCode.KO, ArtworkTranslation.builder()
                    .title(request.koTitle())
                    .description(request.koDescription())
                    .build());
        }

        if (request.enTitle() != null) {
            artwork.addTranslation(LanguageCode.EN, ArtworkTranslation.builder()
                    .title(request.enTitle())
                    .description(request.enDescription())
                    .build());
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
                .status(artwork.getStatus())
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
    public void updateArtwork(Long id, ArtworkUpdateRequest request) {
        // 1. 기존 엔티티 조회
        // 1. 기존 엔티티 조회 (영속성 컨텍스트)
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작품을 찾을 수 없습니다."));

        // 2. 엔티티 내부의 업데이트 메서드 호출 (빌더 대신 핵심 필드 일괄 변경)
        artwork.update(
                request.thumbnailUrl(),
                request.medium(),
                request.size(),
                request.visibility(),
                request.status(),
                new ArrayList<>(request.images()), // 리스트 교체
                parseDate(request.startedAt()),
                parseDate(request.finishedAt())
        );

        // 3. 다국어 정보 업데이트 (Map 구조 활용)
        processTranslations(artwork, request);

    }

    // 상세 업데이트 로직 내부에서 호출
    private void processTranslations(Artwork artwork, ArtworkUpdateRequest request) {
        // 한국어 처리
        updateOrAddTranslation(artwork, LanguageCode.KO, request.koTitle(), request.koDescription());
        // 영어 처리
        updateOrAddTranslation(artwork, LanguageCode.EN, request.enTitle(), request.enDescription());

        // 🚀 나중에 일본어/중국어가 추가되면 여기 한 줄씩만 더 적으면 끝!
        // updateOrAddTranslation(artwork, LanguageCode.JA, request.jaTitle(), request.jaDescription());
    }

    private void updateOrAddTranslation(Artwork artwork, LanguageCode lang, String title, String desc) {
        // 제목이 없으면 번역 데이터로서 가치가 없으므로 스킵 (혹은 기존 데이터 삭제 로직)
        if (title == null || title.isBlank()) return;

        Map<LanguageCode, ArtworkTranslation> translations = artwork.getTranslations();

        if (translations.containsKey(lang)) {
            // 1. 기존에 해당 언어 번역이 있으면 값만 업데이트 (Dirty Checking)
            translations.get(lang).update(title, desc);
        } else {
            // 2. 해당 언어 번역이 처음 들어온 것이라면 빌더로 생성 후 추가
            artwork.addTranslation(lang, ArtworkTranslation.builder()
                    .title(title)
                    .description(desc)
                    .build());
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

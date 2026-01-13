package com.artivefor.me;

import com.artivefor.me.data.artwork.Artwork;
import com.artivefor.me.data.artwork.ArtworkHistory;
import com.artivefor.me.data.artwork.HistoryType;
import com.artivefor.me.data.artwork.WorkStatus;
import com.artivefor.me.repository.artwork.ArtworkHistoryRepository;
import com.artivefor.me.repository.artwork.ArtworkRepository;
import com.artivefor.me.security.config.SecurityConfig;
import com.artivefor.me.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({SecurityConfig.class, JwtTokenProvider.class})
class ArtworkRepositoryTest {

    @Autowired
    private ArtworkRepository artworkRepository;
    @Autowired private ArtworkHistoryRepository historyRepository;

    @Test
    @DisplayName("작품과 히스토리를 성공적으로 저장한다")
    void saveArtworkWithHistories() {
        // Given (픽스처가 있다면 더 깔끔하겠죠?)
        Artwork artwork = Artwork.builder()
                .status(WorkStatus.COMPLETED)
                .medium("Oil on Canvas")
                .build();

        ArtworkHistory history1 = ArtworkHistory.builder()
                .artwork(artwork)
                .imageUrl("step1.png")
                .type(HistoryType.MANUAL)
                .build();

        // When
        Artwork saved = artworkRepository.save(artwork);
        historyRepository.save(history1);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMedium()).isEqualTo("Oil on Canvas");

        List<ArtworkHistory> histories = historyRepository.findByArtworkIdOrderByCreatedAtDesc(saved.getId());
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getImageUrl()).isEqualTo("step1.png");
    }

    @Test
    @DisplayName("동작 로그 확인을 위한 작품 저장 테스트")
    void createArtworkWithLogTest() {
        // 1. 데이터 준비
        Artwork artwork = Artwork.builder()
                .status(WorkStatus.IN_PROGRESS)
                .medium("Digital Art")
                .build();

        // 2. 실행 (여기서 SQL 로그가 집중적으로 찍힙니다)
        System.out.println("=== 저장 시작 ===");
        Artwork saved = artworkRepository.save(artwork);

        ArtworkHistory history = ArtworkHistory.builder()
                .artwork(saved)
                .imageUrl("https://artive.com/thumb.png")
                .build();
        historyRepository.save(history);

        artworkRepository.flush(); // DB에 즉시 반영해서 로그 강제 출력
        System.out.println("=== 저장 완료 ===");

        // 3. 다시 조회해서 눈으로 확인
        System.out.println("=== 조회 시작 ===");
        Artwork found = artworkRepository.findById(saved.getId()).get();
        System.out.println("작품 ID: " + found.getId());
        System.out.println("작품 상태: " + found.getStatus());
    }
}
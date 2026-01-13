package com.artivefor.me.repository.artwork;

import com.artivefor.me.data.artwork.ArtworkHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkHistoryRepository extends JpaRepository<ArtworkHistory, Long> {
    // 특정 작품의 모든 히스토리를 최신순으로 가져오기
    List<ArtworkHistory> findByArtworkIdOrderByCreatedAtDesc(Long artworkId);

    // 특정 작품의 히스토리를 생성일 역순으로 페이징 조회
    Page<ArtworkHistory> findByArtworkIdOrderByCreatedAtDesc(Long artworkId, Pageable pageable);
}
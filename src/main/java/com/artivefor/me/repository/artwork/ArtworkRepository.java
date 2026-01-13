package com.artivefor.me.repository.artwork;

import com.artivefor.me.data.artwork.Artwork;
import com.artivefor.me.data.common.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    // 특정 유저의 작품을 최신순으로 페이징 조회
    Page<Artwork> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    // 1. 특정 작가의 작품 목록 (관리자 페이지에서 작가 상세 조회 시 사용)
    Page<Artwork> findByAuthorId(Long userId, Pageable pageable);

    // 2. 가시성 기준 필터링 (나만 보기/전체 공개 모아보기)
    Page<Artwork> findByVisibility(Visibility visibility, Pageable pageable);

    // 3. 관리자용: 작품 제목으로 검색 (다국어 테이블 조인 검색)
    @Query("SELECT a FROM Artwork a JOIN a.translations t " +
            "WHERE t.title LIKE %:keyword% " +
            "AND KEY(a.translations) = com.artivefor.me.data.common.LanguageCode.KO")
    Page<Artwork> searchByKoreanTitle(@Param("keyword") String keyword, Pageable pageable);


}
package com.artivefor.me.repository.user;

import com.artivefor.me.data.user.ArtiveUser;
import com.artivefor.me.data.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtiveUserRepository extends JpaRepository<ArtiveUser, Long> {

    // 1. 기본 조회 (로그인 및 프로필 조회용)
    Optional<ArtiveUser> findByEmail(String email);
    Optional<ArtiveUser> findBySlug(String slug);

    // 2. 관리자용: 권한별 유저 목록 페이징 (리액트 DataTable 연동용)
    Page<ArtiveUser> findByRole(Role role, Pageable pageable);

    // 3. 관리자용: 이메일이나 슬러그로 유저 검색
    @Query("SELECT u FROM ArtiveUser u WHERE u.email LIKE %:keyword% OR u.slug LIKE %:keyword%")
    Page<ArtiveUser> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 4. 통계용: 전체 유저 수나 특정 권한 유저 수 확인
    long countByRole(Role role);

    boolean existsByEmail(String email);

    boolean existsBySlug(String slug);
}
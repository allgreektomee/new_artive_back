package com.artivefor.me.repository.common;


import com.artivefor.me.data.common.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    // 1. 기본 조회: 가장 최신(또는 첫 번째) 설정 불러오기
    // 보통 관리자 페이지 초기 로드나 앱 기동 시 사용합니다.
    @Query("SELECT s FROM SystemConfig s WHERE s.id = 1")
    Optional<SystemConfig> findMainConfig();

    // 2. 관리자용: 특정 설정 존재 여부 확인
    // (만약 ID가 1이 아닌 다른 방식으로 관리할 경우를 대비)
    boolean existsById(Long id);

    // 3. 보안/감사: 특정 점검 상태인 설정이 있는지 확인 (통계/체크용)
    long countByIsMaintenanceTrue();
}
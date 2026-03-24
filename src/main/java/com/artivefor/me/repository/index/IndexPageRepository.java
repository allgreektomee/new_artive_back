package com.artivefor.me.repository.index;

import com.artivefor.me.data.index.IndexPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IndexPageRepository extends JpaRepository<IndexPage, Long> {

    // 가장 최근에 등록/수정된 인덱스 페이지 설정 하나를 가져옴
    Optional<IndexPage> findTopByOrderByIdDesc();
}
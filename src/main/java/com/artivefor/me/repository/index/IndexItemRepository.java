package com.artivefor.me.repository.index;

import com.artivefor.me.data.index.IndexItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IndexItemRepository extends JpaRepository<IndexItem, Long> {

    // 특정 인덱스 페이지에 속한 아이템들을 순서대로 가져옴
    List<IndexItem> findAllByIndexPageIdOrderByNoAsc(Long indexPageId);
}
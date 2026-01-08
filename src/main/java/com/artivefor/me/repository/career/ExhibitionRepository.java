package com.artivefor.me.repository.career;

import com.artivefor.me.data.career.Exhibition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    Page<Exhibition> findByAuthorId(Long userId, Pageable pageable);
}
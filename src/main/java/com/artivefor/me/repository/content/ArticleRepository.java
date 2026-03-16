package com.artivefor.me.repository.content;

import com.artivefor.me.data.content.Article;
import com.artivefor.me.data.content.ContentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByType(ContentType type, Pageable pageable);
}

package com.artivefor.me.repository.content;

import com.artivefor.me.data.common.Category;
import com.artivefor.me.data.common.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByTypeOrderByDisplayOrderAsc(CategoryType type);
}

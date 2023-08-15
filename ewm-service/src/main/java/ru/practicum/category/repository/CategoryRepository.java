package ru.practicum.category.repository;

import ru.practicum.category.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> getAll(int from, int size);

    List<Category> findByIds(List<Long> categoryIds);

    Category findById(Long categoryId);

    Category add(Category category);

    Category update(Category category);

    void delete(long catId);
}

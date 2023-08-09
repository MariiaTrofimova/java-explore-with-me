package ru.practicum.category.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.category.Category;

import java.util.List;

@Repository
@Slf4j
public class CategoryRepositoryImpl implements CategoryRepository {
    @Override
    public List<Category> getAll(int from, int size) {
        return null;
    }

    @Override
    public List<Category> findByIds(List<Long> categoryIds) {
        return null;
    }

    @Override
    public Category findById(Long categoryId) {
        return null;
    }

    @Override
    public Category add(Category category) {
        return null;
    }

    @Override
    public Category update(Category category) {
        return null;
    }

    @Override
    public void delete(long catId) {

    }
}

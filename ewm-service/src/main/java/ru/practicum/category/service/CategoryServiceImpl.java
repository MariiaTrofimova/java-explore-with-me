package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.repository.CategoryRepository;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Integer from, Integer size) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findById(long catId) {
        return null;
    }

    @Override
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        return null;
    }

    @Override
    public CategoryDto patch(long catId, NewCategoryDto newCategoryDto) {
        return null;
    }

    @Override
    public void delete(long catId) {

    }
}

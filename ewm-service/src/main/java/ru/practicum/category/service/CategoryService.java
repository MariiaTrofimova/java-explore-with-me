package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto findById(long catId);

    CategoryDto add(NewCategoryDto newCategoryDto);

    CategoryDto patch(long catId, NewCategoryDto newCategoryDto);

    void delete(long catId);
}

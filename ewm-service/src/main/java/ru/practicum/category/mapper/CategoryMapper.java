package ru.practicum.category.mapper;

import ru.practicum.category.Category;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static Category toCategory(NewCategoryDto newDto) {
        return Category.builder()
                .name(newDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static List<CategoryDto> toCategoryDto(List<Category> categories) {
        return categories.stream().map(category -> CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build()).collect(Collectors.toList());
    }
}

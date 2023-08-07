package ru.practicum.category.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
public class NewCategoryDto {
    @Size(min = 1, max = 50, message = "Название категории может быть от 1 до 50 символов")
    String name;
}

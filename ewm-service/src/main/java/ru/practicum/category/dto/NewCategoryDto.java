package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NewCategoryDto {
    @Size(min = 1, max = 50, message = "Название категории может быть от 1 до 50 символов")
    String name;
}

package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NewLocationDto {
    @NotNull(message = "Локация должна содержать данные о широте")
    @Min(value = -90, message = "Широта не может быть меньше -90")
    @Max(value = 90, message = "Широта не может быть больше 90")
    private Float lat;
    @NotNull(message = "Локация должна содержать данные о широте")
    @Min(value = -180, message = "Долгота не может быть меньше -180")
    @Max(value = 180, message = "Долгота не может быть больше 180")
    private Float lon;
    @NotNull(message = "Локация должна содержать данные о радиусе")
    @PositiveOrZero(message = "Радиус локации не может быть отрицательным")
    private Integer radius;
    @NotBlank(message = "Название локации не может быть пустым")
    @Size(min = 1, max = 50, message = "Название локации может быть от 1 до 50 символов")
    private String name;
    @NotBlank(message = "Тип локации не может быть пустым")
    private String type;
}

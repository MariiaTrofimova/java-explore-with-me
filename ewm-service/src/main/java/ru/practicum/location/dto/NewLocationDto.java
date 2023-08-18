package ru.practicum.location.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NewLocationDto {
    @NotNull(message = "Локация должна содержать данные о широте")
    @PositiveOrZero(message = "Широта не может быть отрицательной")
    private Float lat;
    @NotNull(message = "Локация должна содержать данные о широте")
    @PositiveOrZero(message = "Долгота не может быть отрицательной")
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

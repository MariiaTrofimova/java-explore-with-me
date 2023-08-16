package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import static ru.practicum.util.ValidationGroups.Create;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class LocationFullDto {
    private long id;
    @NotNull(groups = Create.class, message = "Локация должна содержать данные о широте")
    @PositiveOrZero(message = "Широта не может быть отрицательной")
    private Float lat;
    @NotNull(groups = Create.class, message = "Локация должна содержать данные о широте")
    @PositiveOrZero(message = "Долгота не может быть отрицательной")
    private Float lon;
    @NotNull(groups = Create.class, message = "Локация должна содержать данные о радиусе")
    @PositiveOrZero(message = "Радиус локации не может быть отрицательным")
    private Integer radius;
    @NotBlank(groups = Create.class, message = "Название локации не может быть пустым")
    @Size(groups = Create.class, min = 1, max = 50, message = "Название локации может быть от 1 до 50 символов")
    private String name;
    @NotBlank(groups = Create.class, message = "Тип локации не может быть пустым")
    private String type;
}
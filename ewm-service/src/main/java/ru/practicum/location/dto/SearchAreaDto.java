package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SearchAreaDto {
    @NotNull(message = "Область поиска должна содержать данные о широте")
    @PositiveOrZero(message = "Широта не может быть отрицательной")
    private Float lat;
    @NotNull(message = "Область поиска должна содержать данные о широте")
    @PositiveOrZero(message = "Долгота не может быть отрицательной")
    private Float lon;
    @NotNull(message = "Область поиска должна содержать данные о радиусе")
    @Positive(message = "Радиус области поиска должен быть положительным")
    private Integer radius;
}

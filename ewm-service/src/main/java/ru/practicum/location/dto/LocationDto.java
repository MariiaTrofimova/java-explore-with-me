package ru.practicum.location.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
public class LocationDto {
    @NotNull(message = "Локация должна содержать данные о широте")
    @PositiveOrZero(message = "Широта не может быть отрицательной")
    private Float lat;
    @NotNull(message = "Локация должна содержать данные о широте")
    @PositiveOrZero(message = "Широта не может быть отрицательной")
    private Float lon;
}
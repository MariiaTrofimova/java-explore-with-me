package ru.practicum.location.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class LocationDto {
    @NotNull(message = "Локация должна содержать данные о широте")
    @Min(value = -90, message = "Широта не может быть меньше -90")
    @Max(value = 90, message = "Широта не может быть больше 90")
    private Float lat;
    @NotNull(message = "Локация должна содержать данные о широте")
    @Min(value = -180, message = "Долгота не может быть меньше -180")
    @Max(value = 180, message = "Долгота не может быть больше 180")
    private Float lon;
}
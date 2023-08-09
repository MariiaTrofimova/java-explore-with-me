package ru.practicum.event.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class LocationDto {
    @NotNull(message = "Локация должна содержать данные о широте")
    private float lat;
    @NotNull(message = "Локация должна содержать данные о широте")
    private float lon;
}
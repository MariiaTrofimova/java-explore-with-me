package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class LocationFullDto {
    private Long id;
    @Min(value = -90, message = "Широта не может быть меньше -90")
    @Max(value = 90, message = "Широта не может быть больше 90")
    private Float lat;
    @Min(value = -180, message = "Долгота не может быть меньше -180")
    @Max(value = 180, message = "Долгота не может быть больше 180")
    private Float lon;
    @PositiveOrZero(message = "Радиус локации не может быть отрицательным")
    private Integer radius;
    private String name;
    private String type;
}
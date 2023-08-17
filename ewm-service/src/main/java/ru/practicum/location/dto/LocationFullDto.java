package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class LocationFullDto {
    private Long id;
    @PositiveOrZero(message = "Широта не может быть отрицательной")
    private Float lat;
    @PositiveOrZero(message = "Долгота не может быть отрицательной")
    private Float lon;
    @PositiveOrZero(message = "Радиус локации не может быть отрицательным")
    private Integer radius;
    private String name;
    private String type;
}
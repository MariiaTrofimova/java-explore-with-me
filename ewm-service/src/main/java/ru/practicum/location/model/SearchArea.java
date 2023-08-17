package ru.practicum.location.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SearchArea {
    private Float lat;
    private Float lon;
    private Integer radius;
}

package ru.practicum.location.model;

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

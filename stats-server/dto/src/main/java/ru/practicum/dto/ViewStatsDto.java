package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewStatsDto implements Comparable<ViewStatsDto> {
    private String app;
    private String uri;
    private long hits;

    @Override
    public int compareTo(ViewStatsDto o) {
        return (int) (o.getHits() - hits);
    }
}
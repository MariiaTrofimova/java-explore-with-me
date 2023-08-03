package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

@Data
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

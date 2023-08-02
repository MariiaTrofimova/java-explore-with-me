package ru.practicum.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ViewStatsListDto {
    private final List<ViewStatsDto> viewStatsDtoList;

    public ViewStatsListDto() {
        this.viewStatsDtoList = new ArrayList<>();
    }
}

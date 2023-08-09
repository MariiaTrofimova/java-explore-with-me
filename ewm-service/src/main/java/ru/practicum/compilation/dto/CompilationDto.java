package ru.practicum.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.event.dto.EventShortDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CompilationDto {
    private long id;
    private boolean pinned;
    private String title;

    private final List<EventShortDto> events = new ArrayList<>();

    public void addEvents(List<EventShortDto> eventShortDtos) {
        events.addAll(eventShortDtos);
    }
}
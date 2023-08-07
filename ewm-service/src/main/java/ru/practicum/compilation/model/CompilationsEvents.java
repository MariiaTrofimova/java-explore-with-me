package ru.practicum.compilation.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompilationsEvents {
    private long compilationId;
    private long eventId;
}
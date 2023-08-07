package ru.practicum.compilation.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UpdateCompilationRequest {
    Boolean pinned;
    String title;
    private final List<Long> events = new ArrayList<>();
}

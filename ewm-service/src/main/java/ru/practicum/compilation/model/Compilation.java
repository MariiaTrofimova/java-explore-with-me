package ru.practicum.compilation.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Compilation {
    private long id;
    private String title;
    private boolean pinned;
}
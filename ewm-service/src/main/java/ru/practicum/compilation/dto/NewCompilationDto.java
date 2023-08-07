package ru.practicum.compilation.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class NewCompilationDto {
    boolean pinned;
    @Size(min = 1, max = 50, message =  "Название подборки может быть от 1 до 50 символов")
    String title;
    private final List<Long> events = new ArrayList<>();
}
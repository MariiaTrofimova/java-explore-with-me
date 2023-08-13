package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NewCompilationDto {
    boolean pinned;
    @NotBlank(message = "Название подборки не может быть пустым")
    @Size(min = 1, max = 50, message = "Название подборки может быть от 1 до 50 символов")
    String title;
    private final List<Long> events = new ArrayList<>();
}
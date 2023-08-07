package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto findById(long compId);

    CompilationDto add(NewCompilationDto newCompilationDto);

    CompilationDto patch(long compId, UpdateCompilationRequest updateCompilationRequest);

    void delete(long compId);
}

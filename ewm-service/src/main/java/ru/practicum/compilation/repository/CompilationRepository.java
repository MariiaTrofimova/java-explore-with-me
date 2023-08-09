package ru.practicum.compilation.repository;

import ru.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository {
    List<Compilation> getByParams(boolean pinned, int from, int size);

    Compilation findById(long compId);

    Compilation add(Compilation compilation);

    void deleteById(long compId);

    Compilation update(Compilation compilation);

    void clearEventsByCompId(long compId);

    void addEventsByCompId(long compId, List<Long> eventIds);
}

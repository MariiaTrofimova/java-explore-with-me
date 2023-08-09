package ru.practicum.compilation.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;

import java.util.List;

@Repository
@Slf4j
public class CompilationRepositoryImpl implements CompilationRepository {
    @Override
    public List<Compilation> getByParams(boolean pinned, int from, int size) {
        return null;
    }

    @Override
    public Compilation findById(long compId) {
        return null;
    }

    @Override
    public Compilation add(Compilation compilation) {
        return null;
    }

    @Override
    public void deleteById(long compId) {

    }

    @Override
    public Compilation update(Compilation compilation) {
        return null;
    }

    @Override
    public void clearEventsByCompId(long compId) {

    }

    @Override
    public void addEventsByCompId(long compId, List<Long> eventIds) {

    }
}

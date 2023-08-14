package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.error.exceptions.ConflictException;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.StatisticRequestService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.util.Validation.validateStringField;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repository;
    private final CategoryRepository categoryRepo;
    private final EventRepository eventRepo;
    private final RequestRepository requestRepo;
    private final UserRepository userRepo;
    private final StatisticRequestService statsRequestService;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = repository.getByParams(pinned, from, size);
        } else {
            compilations = repository.getAll(from, size);
        }

        if (compilations.isEmpty()) {
            return Collections.emptyList();
        }
        addEventIdsToCompilations(compilations);
        return makeCompilationDtoList(compilations);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto findById(long compId) {
        Compilation compilation = repository.findById(compId);
        addEventIdsToCompilation(compilation);
        return makeCompilationDto(compilation);
    }

    @Override
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        try {
            compilation = repository.add(compilation);
        } catch (RuntimeException e) {
            reportTitleUniqueConflict(e, compilation);
        }
        if (!newCompilationDto.getEvents().isEmpty()) {
            repository.addEventsByCompId(compilation.getId(), compilation.getEvents());
        }
        return makeCompilationDto(compilation);
    }

    @Override
    public CompilationDto patch(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = repository.findById(compId);
        updateNotNullFields(compilation, updateCompilationRequest);
        try {
            compilation = repository.update(compilation);
        } catch (RuntimeException e) {
            reportTitleUniqueConflict(e, compilation);
        }
        List<Long> eventIds = updateCompilationRequest.getEvents();
        if (eventIds != null && !eventIds.isEmpty()) {
            repository.clearEventsByCompId(compId);
            repository.addEventsByCompId(compId, eventIds);
            compilation.addEvents(updateCompilationRequest.getEvents());
        }
        return makeCompilationDto(compilation);
    }

    @Override
    public void delete(long compId) {
        repository.deleteById(compId);
    }

    private void updateNotNullFields(Compilation compilation,
                                     UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            validateStringField(updateCompilationRequest.getTitle(), "название подборки", 1, 50);
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
    }

    private void addEventIdsToCompilation(Compilation compilation) {
        List<Long> eventIds = repository.findEventIdsByCompId(compilation.getId());
        compilation.addEvents(eventIds);
    }

    private void addEventIdsToCompilations(List<Compilation> compilations) {
        if (compilations.isEmpty()) {
            return;
        }
        List<Long> compIds = compilations.stream().map(Compilation::getId).collect(Collectors.toList());
        Map<Long, List<Long>> eventIdsByCompIds = repository.findEventIdsByCompIds(compIds);
        compilations.forEach(compilation -> compilation.addEvents(eventIdsByCompIds.get(compilation.getId())));
    }

    private CompilationDto makeCompilationDto(Compilation compilation) {
        List<Long> eventIds = compilation.getEvents();
        if (eventIds.isEmpty()) {
            return CompilationMapper.toEmptyCompilationDto(compilation);
        }
        List<Event> events = eventRepo.finByIds(eventIds);
        List<Category> categories = getCategoriesByEvents(events);
        List<User> users = getUsersByEvents(events);
        Map<Long, Integer> confirmedRequestsByEventIds =
                requestRepo.countConfirmedRequestsByEventIds(eventIds);
        List<ViewStatsDto> viewStatsDtos = statsRequestService.makeStatRequest(events);

        return CompilationMapper.toCompilationDto(compilation, events,
                categories, users, confirmedRequestsByEventIds, viewStatsDtos);
    }

    private List<CompilationDto> makeCompilationDtoList(List<Compilation> compilations) {
        List<Long> eventIds = getEventIdsByCompilations(compilations);
        if (eventIds.isEmpty()) {
            return CompilationMapper.toEmptyCompilationDto(compilations);
        }
        List<Event> events = eventRepo.finByIds(eventIds);
        List<Category> categories = getCategoriesByEvents(events);
        List<User> users = getUsersByEvents(events);
        Map<Long, Integer> confirmedRequestsByEventIds =
                requestRepo.countConfirmedRequestsByEventIds(eventIds);
        List<ViewStatsDto> viewStatsDtos = statsRequestService.makeStatRequest(events);
        return CompilationMapper.toCompilationDto(compilations, events, categories, users,
                confirmedRequestsByEventIds, viewStatsDtos);
    }

    private List<Long> getEventIdsByCompilations(List<Compilation> compilations) {
        return compilations.stream()
                .map(Compilation::getEvents)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Category> getCategoriesByEvents(List<Event> events) {
        List<Long> categoryIds = events.stream()
                .map(Event::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
        return categoryRepo.findByIds(categoryIds);
    }

    private List<User> getUsersByEvents(List<Event> events) {
        List<Long> userIds = events.stream()
                .map(Event::getInitiator)
                .distinct()
                .collect(Collectors.toList());
        return userRepo.findByIds(userIds);
    }

    private void reportTitleUniqueConflict(RuntimeException e, Compilation compilation) {
        String error = e.getMessage();
        String constraint = "uq_compilation_title";
        if (error.contains(constraint)) {
            error = String.format("Подборка с названием %s уже существует", compilation.getTitle());
            log.warn("Попытка дублирования названия подборки: {}", compilation.getTitle());
            throw new ConflictException(error);
        }
        throw new RuntimeException("Ошибка при передаче данных в БД");
    }
}
package ru.practicum.compilation.mapper;

import ru.practicum.category.Category;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.User;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = Compilation.builder()
                .pinned(newCompilationDto.isPinned())
                .title(newCompilationDto.getTitle())
                .build();
        compilation.addEvents(newCompilationDto.getEvents());
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation,
                                                  List<Event> events,
                                                  List<Category> categories,
                                                  List<User> users,
                                                  Map<Long, Integer> confirmedRequestsByEventId,
                                                  List<ViewStatsDto> viewStatsDtos) {
        CompilationDto compilationDto = toEmptyCompilationDto(compilation);
        List<EventShortDto> eventShortDtos = EventMapper.toEventShortDto(
                events,
                categories,
                users,
                confirmedRequestsByEventId,
                viewStatsDtos
        );
        compilationDto.addEvents(eventShortDtos);
        return compilationDto;
    }

    public static List<CompilationDto> toCompilationDto(List<Compilation> compilations,
                                                        List<Event> events,
                                                        List<Category> categories,
                                                        List<User> users,
                                                        Map<Long, Integer> confirmedRequestsByEventId,
                                                        List<ViewStatsDto> viewStatsDtos) {
        List<EventShortDto> eventShortDtos = EventMapper.toEventShortDto(
                events,
                categories,
                users,
                confirmedRequestsByEventId,
                viewStatsDtos
        );
        Map<Long, EventShortDto> eventDtoByEventIds = eventShortDtos.stream()
                .collect(Collectors.toMap(EventShortDto::getId, Function.identity()));


        return compilations.stream()
                .map(compilation -> {
                    CompilationDto compilationDto = toEmptyCompilationDto(compilation);
                    List<EventShortDto> eventShortDtoForCompilation =
                            compilation.getEvents().stream()
                                    .map(eventDtoByEventIds::get).collect(Collectors.toList());
                    compilationDto.addEvents(eventShortDtoForCompilation);
                    return compilationDto;
                })
                .collect(Collectors.toList());
    }

    public static CompilationDto toEmptyCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static List<CompilationDto> toEmptyCompilationDto(List<Compilation> compilations) {
        return compilations.stream().map(CompilationMapper::toEmptyCompilationDto).collect(Collectors.toList());
    }
}
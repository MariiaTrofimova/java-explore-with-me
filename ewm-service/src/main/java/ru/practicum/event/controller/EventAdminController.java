package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.error.util.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.error.util.ErrorMessages.SIZE_ERROR_MESSAGE;
import static ru.practicum.util.DateTime.parseEncodedDateTime;
import static ru.practicum.util.Validation.validateStartEndDates;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService service;

    @GetMapping
    public List<EventFullDto> getAll(@RequestParam List<Long> users,
                                     @RequestParam(name = "states") List<String> statesParams,
                                     @RequestParam List<Long> categories,
                                     @RequestParam String rangeStart,
                                     @RequestParam String rangeEnd,
                                     @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @Positive(message = SIZE_ERROR_MESSAGE)
                                     @RequestParam(defaultValue = "10") Integer size) {
        List<EventState> states = statesParams.stream()
                .map(stateParam -> EventState.from(stateParam)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam)))
                .collect(Collectors.toList());
        Instant start = parseEncodedDateTime(rangeStart);
        Instant end = parseEncodedDateTime(rangeEnd);
        validateStartEndDates(start, end);
        return service.getAllByFiltersByAdmin(users, states, categories, start, end, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patch(@PathVariable long eventId,
                              @RequestBody UpdateEventDto updateEventDto) {
        return service.patchByAdmin(eventId, updateEventDto);
    }
}

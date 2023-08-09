package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.enums.EventSort;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.Instant;
import java.util.List;

import static ru.practicum.error.util.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.error.util.ErrorMessages.SIZE_ERROR_MESSAGE;
import static ru.practicum.util.DateTime.parseEncodedDateTime;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {
    private static final Instant NOW = Instant.now();

    private final EventService service;

    @GetMapping
    public List<EventFullDto> getByFiltersPublic(@RequestParam String text,
                                                 @RequestParam List<Long> categories,
                                                 @RequestParam Boolean paid,
                                                 @RequestParam String rangeStart,
                                                 @RequestParam String rangeEnd,
                                                 @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                 @RequestParam(name = "sort") String sortParam,
                                                 @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive(message = SIZE_ERROR_MESSAGE)
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 HttpServletRequest request) {
        EventSort sort = null;
        if (sortParam != null) {
            sort = EventSort.from(sortParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + sortParam));
        }

        Instant start = rangeStart == null ? NOW : parseEncodedDateTime(rangeStart);
        Instant end = rangeEnd == null ? null : parseEncodedDateTime(rangeEnd);
        String ip = request.getRemoteAddr();
        return service.getByFiltersPublic(text, categories, paid, start, end, onlyAvailable, sort, from, size, ip);
    }

    @GetMapping("/{id}")
    public EventFullDto getByIdPublic(@PathVariable long id,
                                      HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return service.getByIdPublic(id, ip);
    }
}
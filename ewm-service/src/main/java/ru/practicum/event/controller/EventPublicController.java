package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.enums.EventSort;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.error.util.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.error.util.ErrorMessages.SIZE_ERROR_MESSAGE;
import static ru.practicum.util.DateTime.toInstant;
import static ru.practicum.util.Validation.validateStartEndDates;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {
    private final EventService service;

    @GetMapping
    public List<EventFullDto> getByFiltersPublic(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false, name = "rangeStart")
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                 LocalDateTime startLocal,
                                                 @RequestParam(required = false, name = "rangeEnd")
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                 LocalDateTime endLocal,
                                                 @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                 @RequestParam(required = false, name = "sort") String sortParam,
                                                 @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive(message = SIZE_ERROR_MESSAGE)
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 HttpServletRequest request) {
        EventSort sort = null;
        if (sortParam != null) {
            sort = EventSort.from(sortParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown sort parameter: " + sortParam));
        }
        Instant start = startLocal == null ? Instant.now() : toInstant(startLocal);
        Instant end = endLocal == null ? null : toInstant(endLocal);

        if (end != null) {
            validateStartEndDates(start, end);
        }
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
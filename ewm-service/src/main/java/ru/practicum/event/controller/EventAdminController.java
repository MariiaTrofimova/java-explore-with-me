package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.service.EventService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
@RequestMapping("/admin/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService service;

    @GetMapping
    public List<EventFullDto> getAll(@RequestParam(required = false) List<Long> users,
                                     @RequestParam(required = false) List<String> states,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(required = false, name = "rangeStart")
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                     LocalDateTime startLocal,
                                     @RequestParam(required = false, name = "rangeEnd")
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                     LocalDateTime endLocal,
                                     @Min(value = -90, message = "Широта не может быть меньше -90")
                                     @Max(value = 90, message = "Широта не может быть больше 90")
                                     @RequestParam(required = false) Float lat,
                                     @Min(value = -180, message = "Долгота не может быть меньше -180")
                                     @Max(value = 180, message = "Долгота не может быть больше 180")
                                     @RequestParam(required = false) Float lon,
                                     @PositiveOrZero(message = "Радиус поиска должен быть положительным")
                                     @RequestParam(required = false) Integer radius,
                                     @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @Positive(message = SIZE_ERROR_MESSAGE)
                                     @RequestParam(defaultValue = "10") Integer size) {

        if (states != null) {
            states.forEach(stateParam -> EventState.from(stateParam)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam)));
        }
        Instant start = startLocal == null ? null : toInstant(startLocal);
        Instant end = endLocal == null ? null : toInstant(endLocal);
        if (start != null && end != null) {
            validateStartEndDates(start, end);
        }
        return service.getAllByCriteriaByAdmin(users, states, categories, start, end, lat, lon, radius, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patch(@PathVariable long eventId,
                              @RequestBody UpdateEventDto updateEventDto) {
        return service.patchByAdmin(eventId, updateEventDto);
    }
}

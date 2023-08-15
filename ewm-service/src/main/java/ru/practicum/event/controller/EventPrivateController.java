package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.error.util.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.error.util.ErrorMessages.SIZE_ERROR_MESSAGE;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService service;
    private final RequestService requestService;

    @GetMapping
    public List<EventFullDto> getByUserId(@PathVariable long userId,
                                          @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @Positive(message = SIZE_ERROR_MESSAGE)
                                          @RequestParam(defaultValue = "10") Integer size) {
        return service.getByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUsersEventById(@PathVariable long userId,
                                          @PathVariable long eventId) {
        return service.getUsersEventById(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto add(@PathVariable long userId,
                            @Valid @RequestBody NewEventDto newEventDto) {
        return service.add(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long userId,
                               @PathVariable long eventId,
                               @Valid @RequestBody UpdateEventDto updateEventDto) {

        return service.update(userId, eventId, updateEventDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForUsersEvent(@PathVariable long userId,
                                                                  @PathVariable long eventId) {
        return requestService.getRequestsForUsersEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable long userId,
                                                         @PathVariable long eventId,
                                                         @Valid @RequestBody EventRequestStatusUpdateRequest
                                                                 eventRequestStatusUpdateRequest) {
        return requestService.updateRequests(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
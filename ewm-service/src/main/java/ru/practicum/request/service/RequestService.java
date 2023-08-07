package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getAllByUserId(long userId);

    ParticipationRequestDto add(long userId, Long eventId);

    ParticipationRequestDto cancel(long userId, long requestId);
}

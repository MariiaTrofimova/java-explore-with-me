package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.repository.RequestRepository;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllByUserId(long userId) {
        return null;
    }

    @Override
    public ParticipationRequestDto add(long userId, Long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto cancel(long userId, long requestId) {
        return null;
    }
}

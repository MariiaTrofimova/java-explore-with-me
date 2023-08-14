package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.exceptions.ConflictException;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.Request;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.repository.RequestRepository;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.practicum.request.enums.RequestStatus.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final EventRepository eventRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllByUserId(long userId) {
        List<Request> requests = repository.getAllByRequesterId(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return RequestMapper.toParticipationRequestDto(requests);
    }

    @Override
    public ParticipationRequestDto add(long userId, long eventId) {
        Event event = eventRepo.findById(eventId);
        validateRequest(event, eventId, userId);
        Request request = Request.builder()
                .requesterId(userId)
                .eventId(eventId)
                .build();
        int confirmedRequests = repository.countConfirmedRequestsByEventId(eventId);
        int limit = event.getParticipantLimit();
        if (!event.isRequestModeration() && (confirmedRequests < limit) || limit == 0) {
            request.setStatus(CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        try {
            request = repository.add(request);
        } catch (RuntimeException e) {
            reportRequesterEventUniqueConflict(e, request);
        }
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public ParticipationRequestDto cancel(long userId, long requestId) {
        Request request = repository.findById(requestId);
        if (request.getRequesterId() != userId) {
            log.warn("Редактирование запроса с id {} недоступно для пользователя с id {}", requestId, userId);
            throw new NotFoundException(
                    String.format("Редактирование запроса с id %d недоступно для пользователя с id %d",
                            requestId, userId));
        }
        repository.cancel(requestId);
        request.setStatus(CANCELED);
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsForUsersEvent(long userId, long eventId) {
        Event event = eventRepo.findById(eventId);
        if (event.getInitiator() != userId) {
            log.warn("Пользователь с id {} не организатор события с id {}", userId, eventId);
            throw new ValidationException(
                    String.format("Пользователь с id %d не организатор события с id %d", userId, eventId));
        }
        List<Request> requests = repository.findByEventId(eventId);
        return RequestMapper.toParticipationRequestDto(requests);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequests(long userId,
                                                         long eventId,
                                                         EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        String statusParam = eventRequestStatusUpdateRequest.getStatus();
        RequestStatus newStatus = RequestStatus.from(statusParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown status: " + statusParam));

        Event event = eventRepo.findById(eventId);

        int participantLimit = event.getParticipantLimit();
        if (participantLimit == 0 || !event.isRequestModeration()) {
            log.warn("Событие с id {} не требует одобрения заявок", event.getId());
            throw new ConflictException(String.format("Событие с id %d не требует одобрения заявок", event.getId()));
        }

        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        List<Request> requestsToUpdate = repository.findByIds(requestIds);

        requestsToUpdate.forEach(request -> {
            if (request.getEventId() != eventId) {
                log.warn("Заявка с id {} не относится к событию с id {}", request.getId(), eventId);
                throw new NotFoundException(
                        String.format("Заявка с id %d не относится к событию с id %d", request.getId(), eventId));
            }
            if (request.getStatus() != RequestStatus.PENDING) {
                log.warn("Попытка изменить статус у заявок не в состоянии ожидания");
                throw new ConflictException("Можно изменить статус только у заявок, находящихся в состоянии ожидания");
            }
        });

        if (newStatus == CONFIRMED) {
            int confirmedBeforeRequestsQty = repository.countConfirmedRequestsByEventId(eventId);
            if (confirmedBeforeRequestsQty >= event.getParticipantLimit()) {
                reportLimitConflict(eventId, participantLimit);
            }

            List<Request> confirmedRequests;
            List<Request> rejectedRequests = new ArrayList<>();

            int requestsQty = requestsToUpdate.size();
            int freeQtyToConfirm = participantLimit - confirmedBeforeRequestsQty;

            if (freeQtyToConfirm >= requestsQty) {
                requestsToUpdate.forEach(request -> request.setStatus(CONFIRMED));
                confirmedRequests = requestsToUpdate;
                repository.updateStatuses(requestIds, CONFIRMED);
            } else {
                IntStream.range(0, freeQtyToConfirm).forEach(i -> requestsToUpdate.get(i).setStatus(CONFIRMED));
                IntStream.range(freeQtyToConfirm, requestsQty).forEach(i -> requestsToUpdate.get(i).setStatus(REJECTED));
                confirmedRequests = requestsToUpdate.stream().limit(freeQtyToConfirm).collect(Collectors.toList());
                rejectedRequests = requestsToUpdate.stream().skip(freeQtyToConfirm).collect(Collectors.toList());
                repository.updateStatuses(confirmedRequests.stream()
                        .map(Request::getId).collect(Collectors.toList()), CONFIRMED);
                repository.updateStatuses(rejectedRequests.stream()
                        .map(Request::getId).collect(Collectors.toList()), REJECTED);
            }

            return RequestMapper.toEventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);

        } else if (newStatus == REJECTED) {
            requestsToUpdate.forEach(request -> request.setStatus(REJECTED));
            repository.updateStatuses(requestIds, REJECTED);
            return RequestMapper.toEventRequestStatusUpdateResult(Collections.emptyList(), requestsToUpdate);
        } else {
            log.warn("Нельзя изменить статус на PENDING");
            throw new ValidationException("Нельзя изменить статус на PENDING");
        }
    }

    private void validateRequest(Event event, long eventId, long userId) {
        int confirmedRequests = repository.countConfirmedRequestsByEventId(eventId);
        int limit = event.getParticipantLimit();
        if (limit != 0 && confirmedRequests >= limit) {
            reportLimitConflict(eventId, event.getParticipantLimit());
        }
        if (event.getInitiator() == userId) {
            log.warn("Попытка запроса на участие в событии с id {} от инициатора с id {}",
                    eventId, userId);
            throw new ConflictException(
                    String.format("Инициатор события с id %d не может добавить запрос на участие в своём событии с id %d",
                            userId, eventId));
        }
        if (event.getEventState() != EventState.PUBLISHED) {
            log.warn("Попытка заявки на участие в неопубликованном событии с id {} от пользователя с id {}",
                    eventId, userId);
            throw new ConflictException(
                    String.format("Нельзя участвовать в неопубликованном событии с id %d", eventId));
        }
    }

    private void reportLimitConflict(long eventId, int participantLimit) {
        log.warn("У события с id {} достигнут лимит запросов на участие {}",
                eventId, participantLimit);
        throw new ConflictException(
                String.format("У события с id  %d достигнут лимит запросов на участие %d",
                        eventId, participantLimit));
    }

    private void reportRequesterEventUniqueConflict(RuntimeException e, Request request) {
        String error = e.getMessage();
        String constraint = "uq_requester_id_by_event_id";
        if (error.contains(constraint)) {
            error = String.format("Нельзя добавить повторный запрос на участие событии с id %d", request.getEventId());
            log.warn("Попытка повторного запроса на участие в событии с id {} от пользователя с id {}",
                    request.getEventId(), request.getRequesterId());
            throw new ConflictException(error);
        }
        throw new RuntimeException("Ошибка при передаче данных в БД");
    }
}
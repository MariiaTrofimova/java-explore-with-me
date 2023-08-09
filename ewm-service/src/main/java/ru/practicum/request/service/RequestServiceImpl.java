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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        int requestsQty = 1;
        validateParticipationLimit(event, eventId, requestsQty);
        Request request = Request.builder()
                .requesterId(userId)
                .eventId(eventId)
                .build();
        if (!event.isRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        request = repository.add(request);
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
        repository.deleteById(requestId);
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
        Event event = eventRepo.findById(eventId);
        List<Request> requests = repository.findByEventId(eventId);
        if (event.getParticipantLimit() == 0) {
            return RequestMapper.toEventRequestStatusUpdateResult(requests);
        }

        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();

        List<Request> requestsToUpdate = requests.stream()
                .filter(request -> requestIds.contains(request.getId()))
                .collect(Collectors.toList());
        if (requestsToUpdate.stream().anyMatch(request -> request.getStatus() != RequestStatus.PENDING)) {
            log.warn("Попытка изменить статус у заявок не в состоянии ожидания");
            throw new ConflictException("Можно изменить статус только у заявок, находящихся в состоянии ожидания");
        }

        RequestStatus newStatus = eventRequestStatusUpdateRequest.getStatus();
        int requestsConfirmedQty = 0;
        if (newStatus == RequestStatus.CONFIRMED) {
            int requestsQty = requestIds.size();
            requestsConfirmedQty = validateParticipationLimit(event, eventId, requestsQty);
        }
        requestsToUpdate.forEach(request -> request.setStatus(newStatus));

        //если при подтверждении заявки, лимит заявок для события исчерпан,
        //все неподтверждённые заявки необходимо отклонить
        if (requestsConfirmedQty == event.getParticipantLimit()) {
            List<Request> requestsToReject = requests.stream()
                    .filter(request -> request.getStatus() == RequestStatus.PENDING
                            && !requestIds.contains(request.getId()))
                    .peek(request -> request.setStatus(RequestStatus.REJECTED))
                    .collect(Collectors.toList());
            requestsToUpdate.addAll(requestsToReject);
        }
        repository.update(requestsToUpdate);

        return RequestMapper.toEventRequestStatusUpdateResult(requests);
    }

    private void validateRequest(Event event, long eventId, long userId) {
        List<Long> userRequestIds = repository.findByRequestorId(userId);
        if (userRequestIds.contains(eventId)) {
            log.warn("Попытка повторного запроса на участие в событии с id {} от пользователя с id {}",
                    eventId, userId);
            throw new ValidationException(
                    String.format("Нельзя добавить повторный запрос событии с id %d", eventId));
        }
        if (event.getInitiator() == userId) {
            log.warn("Попытка запроса на участие в событии с id {} от инициатора с id {}",
                    eventId, userId);
            throw new ValidationException(
                    String.format("Инициатор события с id %d не может добавить запрос на участие в своём событии с id %d",
                            userId, eventId));
        }
        if (event.getEventState() != EventState.PUBLISHED) {
            log.warn("Попытка заявки на участие в неопубликованном событии с id {} от пользователя с id {}",
                    eventId, userId);
            throw new ValidationException(
                    String.format("Нельзя участвовать в неопубликованном событии с id %d", eventId));
        }
    }

    private int validateParticipationLimit(Event event, long eventId, int requestsQty) {
        int confirmedRequests = repository.countConfirmedRequestsByEventId(eventId);
        int participantLimit = event.getParticipantLimit();
        if (participantLimit != 0 && confirmedRequests + requestsQty > participantLimit) {
            log.warn("У события с id {} достигнут лимит запросов на участие {}",
                    eventId, participantLimit);
            throw new ConflictException(
                    String.format("У события с id  %d достигнут лимит запросов на участие %d",
                            eventId, participantLimit));
        }
        return confirmedRequests + requestsQty;
    }
}
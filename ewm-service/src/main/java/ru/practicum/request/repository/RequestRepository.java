package ru.practicum.request.repository;

import ru.practicum.request.Request;
import ru.practicum.request.enums.RequestStatus;

import java.util.List;
import java.util.Map;

public interface RequestRepository {
    Map<Long, Integer> countConfirmedRequestsByEventIds(List<Long> eventIds);

    int countConfirmedRequestsByEventId(long id);

    Request add(Request request);

    List<Request> getAllByRequesterId(long requesterId);

    Request findById(long requestId);

    void deleteById(long requestId);

    List<Request> findByEventId(long eventId);

    List<Request> findByIds(List<Long> requestIds);

    void updateStatuses(List<Long> requestIds, RequestStatus requestStatus);

    void cancel(long requestId);

    List<Long> findEventIdsByRequestorId(long userId);
}

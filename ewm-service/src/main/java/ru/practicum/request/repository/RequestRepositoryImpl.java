package ru.practicum.request.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.request.Request;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class RequestRepositoryImpl implements RequestRepository {
    @Override
    public Map<Long, Integer> countConfirmedRequestsByEventIds(List<Long> eventIds) {
        return null;
    }

    @Override
    public int countConfirmedRequestsByEventId(long id) {
        return 0;
    }

    @Override
    public Request add(Request request) {
        return null;
    }

    @Override
    public List<Request> getAllByRequesterId(long requesterId) {
        return null;
    }

    @Override
    public List<Long> findByRequestorId(long userId) {
        return null;
    }

    @Override
    public Request findById(long requestId) {
        return null;
    }

    @Override
    public void deleteById(long requestId) {

    }

    @Override
    public List<Request> findByEventId(long eventId) {
        return null;
    }

    @Override
    public List<Request> findByIds(List<Long> requestIds) {
        return null;
    }

    @Override
    public boolean update(List<Request> requestsToUpdate) {
        return false;
    }
}
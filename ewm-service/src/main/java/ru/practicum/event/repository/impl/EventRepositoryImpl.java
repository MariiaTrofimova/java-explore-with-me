package ru.practicum.event.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Criteria;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import java.time.Instant;
import java.util.List;

@Repository
@Slf4j
public class EventRepositoryImpl implements EventRepository {
    @Override
    public List<Event> getByCriteria(Criteria criteria) {
        return null;
    }

    @Override
    public List<Event> getByFilters(List<Long> users, List<EventState> states, List<Long> categories, Instant start, Instant end, int from, int size) {
        return null;
    }

    @Override
    public List<Event> findByInitiatorId(long userId, int from, int size) {
        return null;
    }

    @Override
    public Event add(Event event) {
        return null;
    }

    @Override
    public Event findById(long eventId) {
        return null;
    }

    @Override
    public Event update(Event event) {
        return null;
    }

    @Override
    public long countEventsByCategoryId(long catId) {
        return 0;
    }

    @Override
    public List<Event> finByIds(List<Long> eventIds) {
        return null;
    }
}

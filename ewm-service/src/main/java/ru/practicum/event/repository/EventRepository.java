package ru.practicum.event.repository;

import ru.practicum.event.model.Criteria;
import ru.practicum.event.model.Event;

import java.util.List;

public interface EventRepository {

    List<Event> getByCriteria(Criteria criteria);

    List<Event> findByInitiatorId(long userId, int from, int size);

    Event add(Event event);

    Event findById(long eventId);

    Event update(Event event);

    long countEventsByCategoryId(long catId);

    List<Event> finByIds(List<Long> eventIds);

    void setAvailable(long eventId, boolean available);
}

package ru.practicum.event.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.error.exceptions.ConflictException;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.event.enums.EventSort;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Criteria;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.model.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;


    @Override
    public List<Event> getByCriteria(Criteria criteria) {
        StringBuilder sql = new StringBuilder("select * from events");
        List<String> conditions = new ArrayList<>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (criteria.getPublished() != null) {
            conditions.add("state = 'PUBLISHED'");
        }

        if (criteria.getText() != null) {
            List<String> textParams = List.of("annotation", "description", "title");
            String textConditions = textParams.stream()
                    .map(param -> "lower(" + param + ") like concat('%', :text, '%')")
                    .collect(Collectors.joining(" OR ", "(", ")"));
            conditions.add(textConditions);
            parameters.addValue("text", criteria.getText());
        }

        if (criteria.getPaid() != null) {
            conditions.add("paid = :paid");
            parameters.addValue("paid", criteria.getPaid());
        }

        if (criteria.getStart() != null) {
            conditions.add("event_date >= :start");
            parameters.addValue("start", Timestamp.from(criteria.getStart()));
        }

        if (criteria.getEnd() != null) {
            conditions.add("(event_date <= :end)");
            parameters.addValue("end", Timestamp.from(criteria.getEnd()));
        }

        if (criteria.getUsers() != null) {
            conditions.add("initiator in (:initiators)");
            parameters.addValue("initiators", criteria.getUsers());
        }

        if (criteria.getStates() != null) {
            conditions.add("state in (:states)");
            parameters.addValue("states", criteria.getStates());
        }

        if (criteria.getLocation() != null) {
            Location location = criteria.getLocation();
            conditions.add("distance(:lat, :lon, lat, lon) <= :radius");
            parameters.addValue("lat", location.getLat());
            parameters.addValue("lon", location.getLon());
            parameters.addValue("radius", location.getRadius());
        }

        if (!conditions.isEmpty()) {
            String allConditions = conditions.stream()
                    .collect(Collectors.joining(" AND ", "(", ")"));
            sql.append(" where ").append(allConditions);
        }

        if (criteria.getSort() != null && criteria.getSort() == EventSort.EVENT_DATE) {
            sql.append(" order by event_date");
        }
        if (criteria.getSort() == null || criteria.getSort() != EventSort.VIEWS) {
            sql.append(" limit :size offset :from");
            parameters.addValue("size", criteria.getSize());
            parameters.addValue("from", criteria.getFrom());
        }

        return namedJdbcTemplate.query(sql.toString(), parameters, (rs, rowNum) -> mapRowToEvent(rs));
    }

    @Override
    public List<Event> findByInitiatorId(long userId, int from, int size) {
        String sql = "select * from events where initiator = :userId " +
                "order by id limit :size offset :from";
        MapSqlParameterSource parameters = new MapSqlParameterSource("userId", userId);
        parameters.addValue("from", from);
        parameters.addValue("size", size);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToEvent(rs));
    }

    @Override
    public Event add(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("id", "created_on");
        KeyHolder generatedKeys = simpleJdbcInsert.executeAndReturnKeyHolder(event.toMap());
        Map<String, Object> keys = generatedKeys.getKeys();
        long id = (long) Objects.requireNonNull(keys).get("id");
        Timestamp createdOn = (Timestamp) keys.get("created_on");
        event.setId(id);
        event.setCreatedOn(createdOn.toInstant());
        return event;
    }

    @Override
    public Event findById(long id) {
        String sql = "select * from events where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToEvent(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Событие с id {} не найдено", id);
            throw new NotFoundException(String.format("Событие с id %d не найдено", id));
        }
    }

    @Override
    public Event update(Event event) {
        String sql = "update events set annotation = :annotation, " +
                "category_id = :categoryId, " +
                "description = :description, " +
                "event_date = :eventDate, " +
                "location_id = :locationId, " +
                "paid = :paid, " +
                "participant_limit = :participantLimit, " +
                "request_moderation = :requestModeration, " +
                "title = :title, " +
                "initiator = :initiator, " +
                "created_on = :createdOn ";
        MapSqlParameterSource parameters = makeParameterMap(event);
        if (event.getEventState() == EventState.PUBLISHED) {
            sql = sql + ", published_on = :publishedOn , state = 'PUBLISHED' ";
        } else if (event.getEventState() == EventState.CANCELED) {
            sql = sql + ", state = 'CANCELED' ";
        }
        sql = sql + "where id = :id";
        if (namedJdbcTemplate.update(sql, parameters) > 0) {
            return event;
        }
        log.warn("Событие с id {} не найдено", event.getId());
        throw new NotFoundException(String.format("Событие с id %d не найдено", event.getId()));
    }

    @Override
    public long countEventsByCategoryId(long catId) {
        String sql = "select COUNT(id) as countEventsByCat " +
                "from events where category_id = :catId";
        MapSqlParameterSource parameters = new MapSqlParameterSource("catId", catId);
        Long countEventsByCategoryId = namedJdbcTemplate.queryForObject(sql, parameters, Long.class);
        return countEventsByCategoryId == null ? 0 : countEventsByCategoryId;
    }

    @Override
    public List<Event> finByIds(List<Long> ids) {
        String sql = "select * from events where id in (:ids)";
        MapSqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToEvent(rs));
    }

    @Override
    public long countEventsByLocationId(long locId) {
        String sql = "select COUNT(id) as countEventsByLoc " +
                "from events where location_id = :locId";
        MapSqlParameterSource parameters = new MapSqlParameterSource("locId", locId);
        Long countEventsByLocId = namedJdbcTemplate.queryForObject(sql, parameters, Long.class);
        return countEventsByLocId == null ? 0 : countEventsByLocId;
    }

    private MapSqlParameterSource makeParameterMap(Event event) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", event.getId());
        parameters.addValue("annotation", event.getAnnotation());
        parameters.addValue("categoryId", event.getCategoryId());
        parameters.addValue("description", event.getDescription());
        parameters.addValue("eventDate", Timestamp.from(event.getEventDate()));
        parameters.addValue("locationId", event.getLocationId());
        parameters.addValue("paid", event.isPaid());
        parameters.addValue("participantLimit", event.getParticipantLimit());
        parameters.addValue("requestModeration", event.isRequestModeration());
        parameters.addValue("title", event.getTitle());
        parameters.addValue("initiator", event.getInitiator());
        parameters.addValue("createdOn", Timestamp.from(event.getCreatedOn()));
        Timestamp publishedOnTimestamp = event.getPublishedOn() == null ? null :
                Timestamp.from(event.getPublishedOn());
        parameters.addValue("publishedOn", publishedOnTimestamp);
        parameters.addValue("eventState", event.getEventState());
        return parameters;
    }

    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String annotation = rs.getString("annotation");
        long categoryId = rs.getLong("category_id");
        String description = rs.getString("description");
        Instant eventDate = rs.getTimestamp("event_date").toInstant();
        long locationId = rs.getLong("location_id");
        boolean paid = rs.getBoolean("paid");
        int participantLimit = rs.getInt("participant_limit");
        boolean requestModeration = rs.getBoolean("request_moderation");
        String title = rs.getString("title");
        long initiator = rs.getLong("initiator");
        Instant createdOn = rs.getTimestamp("created_on").toInstant();
        EventState eventState = EventState.from(rs.getString("state"))
                .orElseThrow(() -> new ConflictException("В базу попало что-то не то"));

        Event event = Event.builder()
                .id(id)
                .annotation(annotation)
                .categoryId(categoryId)
                .description(description)
                .eventDate(eventDate)
                .locationId(locationId)
                .paid(paid)
                .participantLimit(participantLimit)
                .requestModeration(requestModeration)
                .title(title)
                .initiator(initiator)
                .createdOn(createdOn)
                .eventState(eventState)
                .build();

        if (eventState == EventState.PUBLISHED) {
            Instant publishedOn = rs.getTimestamp("published_on").toInstant();
            event.setPublishedOn(publishedOn);
        }
        return event;
    }
}
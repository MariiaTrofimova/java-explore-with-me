package ru.practicum.request.repository;

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
import ru.practicum.request.Request;
import ru.practicum.request.enums.RequestStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RequestRepositoryImpl implements RequestRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public Map<Long, Integer> countConfirmedRequestsByEventIds(List<Long> eventIds) {
        String sql = "select event_id, COUNT(id) as requests_qty from requests " +
                "where status = 'CONFIRMED' group by event_id";
        Map<Long, Integer> confirmedRequestsByEventIds = eventIds.stream().
                collect(Collectors.toMap(Function.identity(), eventId -> 0));
        MapSqlParameterSource parameters = new MapSqlParameterSource("eventIds", eventIds);
        namedJdbcTemplate.query(sql, parameters,
                rs -> {
                    long eventId = rs.getLong("event_id");
                    int requestQty = rs.getInt("requests_qty");
                    confirmedRequestsByEventIds.put(eventId, requestQty);
                });
        return confirmedRequestsByEventIds;
    }

    @Override
    public int countConfirmedRequestsByEventId(long eventId) {
        String sql = "select COUNT(id) as requests_qty from requests " +
                "where status = 'CONFIRMED' and event_id = :eventId";
        MapSqlParameterSource parameters = new MapSqlParameterSource("eventId", eventId);
        Integer confirmedRequestsByEventIds = namedJdbcTemplate.queryForObject(sql, parameters, Integer.class);
        return confirmedRequestsByEventIds != null ? confirmedRequestsByEventIds : 0;
    }

    @Override
    public Request add(Request request) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("requests")
                .usingGeneratedKeyColumns("id", "created");
        KeyHolder generatedKeys = simpleJdbcInsert.executeAndReturnKeyHolder(request.toMap());
        Map<String, Object> keys = generatedKeys.getKeys();
        long id = (long) Objects.requireNonNull(keys).get("id");
        Timestamp created = (Timestamp) keys.get("created");
        request.setId(id);
        request.setCreated(created.toInstant());
        return request;
    }

    @Override
    public List<Request> getAllByRequesterId(long requesterId) {
        String sql = "select * from requests where requester_id = :requesterId";
        MapSqlParameterSource parameters = new MapSqlParameterSource("requesterId", requesterId);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToRequest(rs));
    }

    @Override
    public List<Long> findEventIdsByRequestorId(long requesterId) {
        String sql = "select event_id from requests where requester_id = :requesterId";
        MapSqlParameterSource parameters = new MapSqlParameterSource("requesterId", requesterId);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> rs.getLong("event_id"));
    }

    @Override
    public Request findById(long id) {
        String sql = "select * from requests where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToRequest(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Запрос с id {} не найден", id);
            throw new NotFoundException(String.format("Запрос с id %d не найден", id));
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "delete from requests where id = ?";
        if (jdbcTemplate.update(sql, id) < 0) {
            log.warn("Запрос с id {} не найден", id);
            throw new NotFoundException(String.format("Запрос с id %d не найден", id));
        }
    }

    @Override
    public List<Request> findByEventId(long eventId) {
        String sql = "select * from requests where event_id = :eventId";
        MapSqlParameterSource parameters = new MapSqlParameterSource("eventId", eventId);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToRequest(rs));
    }

    @Override
    public List<Request> findByIds(List<Long> requestIds) {
        String sql = "select * from requests where id in (:requestIds)";
        MapSqlParameterSource parameters = new MapSqlParameterSource("requestIds", requestIds);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToRequest(rs));
    }

    @Override
    public void updateStatuses(List<Long> requestIds, RequestStatus requestStatus) {
        String sql = "update requests set status = :status where id in (:requestIds)";
        MapSqlParameterSource parameters = new MapSqlParameterSource("requestIds", requestIds);
        parameters.addValue("status", requestStatus.toString());
        namedJdbcTemplate.update(sql, parameters);
    }

    @Override
    public void cancel(long requestId) {
        String sql = "update requests set status = 'CANCELED' where id = :requestId";
        MapSqlParameterSource parameters = new MapSqlParameterSource("requestId", requestId);
        namedJdbcTemplate.update(sql, parameters);
    }

    private Request mapRowToRequest(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long eventId = rs.getLong("event_id");
        long requesterId = rs.getLong("requester_id");
        Instant created = rs.getTimestamp("created").toInstant();
        RequestStatus requestStatus = RequestStatus.from(rs.getString("status"))
                .orElseThrow(() -> new ConflictException("В базу попало что-то не то"));

        return Request.builder()
                .id(id)
                .eventId(eventId)
                .requesterId(requesterId)
                .created(created)
                .status(requestStatus)
                .build();
    }
}
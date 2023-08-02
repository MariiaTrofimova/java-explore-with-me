package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EndPointHitRepositoryImpl implements EndPointHitRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEndpointHit(EndpointHit endpointHit) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("hits")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(endpointHit.toMap()).longValue();
        endpointHit.setId(id);
    }

    @Override
    public Map<Long, Long> getViewsByAppId(Instant start, Instant end) {
        String sql = "select h.id, COUNT(id) as views from hits h where timestamp between :start and :end";
        MapSqlParameterSource parameters = getParamsWithDates(start, end);
        return getViewsByAppId(sql, parameters);
    }

    @Override
    public Map<Long, Long> getViewsByAppId(Instant start, Instant end, List<Long> appIds) {
        String sql = "select h.id, COUNT(id) as views from hits h where timestamp between :start and :end " +
                "AND app_id in :appIds";
        MapSqlParameterSource parameters = getParamsWithDates(start, end);
        parameters.addValue("appIds", appIds);
        return getViewsByAppId(sql, parameters);
    }

    @Override
    public Map<Long, Long> getUniqueViewsByAppId(Instant start, Instant end) {
        String sql = "select h.id, h.ip COUNT(h.ip) as views from hits h where timestamp between :start and :end " +
                "group by h.ip";
        MapSqlParameterSource parameters = getParamsWithDates(start, end);
        return getViewsByAppId(sql, parameters);
    }

    @Override
    public Map<Long, Long> getUniqueViewsByAppId(Instant start, Instant end, List<Long> appIds) {
        String sql = "select h.id, h.ip COUNT(h.ip) as views from hits h where timestamp between :start and :end " +
                "AND app_id in :appIds" +
                "group by h.ip";
        MapSqlParameterSource parameters = getParamsWithDates(start, end);
        parameters.addValue("appIds", appIds);
        return getViewsByAppId(sql, parameters);
    }

    private MapSqlParameterSource getParamsWithDates(Instant start, Instant end) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", start);
        parameters.addValue("end", end);
        return parameters;
    }

    private Map<Long, Long> getViewsByAppId(String sql, MapSqlParameterSource parameters) {
        final Map<Long, Long> viewsByAppId = new HashMap<>();
        jdbcTemplate.query(sql,
                rs -> {
                    long appId = rs.getLong("app_id");
                    long views = rs.getLong("views");
                    viewsByAppId.put(appId, views);
                }, parameters);
        return viewsByAppId;
    }
}
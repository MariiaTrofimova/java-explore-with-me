package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EndPointHitRepositoryImpl implements EndPointHitRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public EndpointHit addEndpointHit(EndpointHit endpointHit) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("hits")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(endpointHit.toMap()).longValue();
        endpointHit.setId(id);
        return endpointHit;
    }

    @Override
    public Map<Long, Long> getViewsByAppId(Instant start, Instant end) {
        String sql = "select app_id, COUNT(ip) as views from hits " +
                "where (timestamp between :start and :end) " +
                "group by app_id";
        MapSqlParameterSource parameters = getParamsWithDates(start, end);
        return makeQuery(sql, parameters);
    }

    @Override
    public Map<Long, Long> getViewsByAppId(Instant start, Instant end, List<Long> appIds) {
        String sql = "select app_id, COUNT(ip) as views from hits " +
                "where (timestamp between :start and :end) " +
                "AND app_id in (:appIds) " +
                "group by app_id";
        MapSqlParameterSource parameters = getParamsWithDates(start, end);
        parameters.addValue("appIds", appIds);
        return makeQuery(sql, parameters);
    }

    @Override
    public Map<Long, Long> getUniqueViewsByAppId(Instant start, Instant end) {
        String sql = "select app_id, COUNT(DISTINCT(ip)) as views from hits " +
                "where (timestamp between :start and :end) " +
                "group by app_id";
        MapSqlParameterSource parameters = getParamsWithDates(start, end);
        return makeQuery(sql, parameters);
    }

    @Override
    public Map<Long, Long> getUniqueViewsByAppId(Instant start, Instant end, List<Long> appIds) {
        String sql = "select app_id, COUNT(DISTINCT(ip)) as views from hits " +
                "where (timestamp between :start and :end) " +
                "AND app_id in (:appIds) " +
                "group by app_id";
        MapSqlParameterSource parameters = getParamsWithDates(start, end);
        parameters.addValue("appIds", appIds);
        return makeQuery(sql, parameters);
    }

    private MapSqlParameterSource getParamsWithDates(Instant start, Instant end) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", Timestamp.from(start));
        parameters.addValue("end", Timestamp.from(end));
        return parameters;
    }

    private Map<Long, Long> makeQuery(String sql, MapSqlParameterSource parameters) {
        final Map<Long, Long> hitsQtyByAppId = new HashMap<>();
        namedJdbcTemplate.query(sql, parameters,
                rs -> {
                    long appId = rs.getLong("app_id");
                    long views = rs.getLong("views");
                    hitsQtyByAppId.put(appId, views);
                });
        return hitsQtyByAppId;
    }
}
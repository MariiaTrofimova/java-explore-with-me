package ru.practicum.compilation.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.error.exceptions.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CompilationRepositoryImpl implements CompilationRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public List<Compilation> getByParams(boolean pinned, int from, int size) {
        String sql = "select * from compilations where pinned = :pinned " +
                "order by id limit :size offset :from";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("pinned", pinned);
        parameters.addValue("from", from);
        parameters.addValue("size", size);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToCompilation(rs));
    }

    @Override
    public Compilation findById(long id) {
        String sql = "select * from compilations where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToCompilation(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Подборка с id {} не найдена", id);
            throw new NotFoundException(String.format("Подборка с id %d не найдена", id));
        }
    }

    @Override
    public Compilation add(Compilation compilation) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("compilations")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(compilation.toMap()).longValue();
        compilation.setId(id);
        //раскидать подборку?
        return compilation;
    }

    @Override
    public void deleteById(long id) {
        String sql = "delete from compilations where id = ?";
        if (jdbcTemplate.update(sql, id) < 0) {
            reportNotFound(id);
        }
    }

    @Override
    public Compilation update(Compilation compilation) {
        String sql = "update compilations set pinned = :pinned, title = :title where id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", compilation.getId());
        parameters.addValue("pinned", compilation.isPinned());
        parameters.addValue("title", compilation.getTitle());
        if (jdbcTemplate.update(sql, parameters) < 0) {
            reportNotFound(compilation.getId());
        }
        return compilation;
    }

    @Override
    public void clearEventsByCompId(long compId) {
        String sql = "delete from compilations_events where compilation_id = ?";
        jdbcTemplate.update(sql, compId);
    }

    @Override
    public void addEventsByCompId(long compId, List<Long> eventIds) {
        StringBuilder sql = new StringBuilder("insert into compilations_events(compilation_id, event_id) values ");
        MapSqlParameterSource parameters = new MapSqlParameterSource("compId", compId);
        String values = IntStream.range(0, eventIds.size())
                .peek(i -> parameters.addValue("eventId" + i, eventIds.get(i)))
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(", ", "(:compId, :eventId", ")"));
        sql.append(values);
        namedJdbcTemplate.update(sql.toString(), parameters);
    }

    @Override
    public List<Long> findEventIdsByCompId(long compId) {
        String sql = "select event_id from compilations_events where compilation_id = :compId";
        MapSqlParameterSource parameters = new MapSqlParameterSource("compId", compId);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> rs.getLong("event_id"));
    }

    @Override
    public Map<Long, List<Long>> findEventIdsByCompIds(List<Long> compIds) {
        String sql = "select compilation_id, event_id from compilations_events where compilation_id in (:compIds)";
        MapSqlParameterSource parameters = new MapSqlParameterSource("compIds", compIds);
        final Map<Long, List<Long>> eventIdsByCompIds = new HashMap<>();

        namedJdbcTemplate.query(sql, parameters,
                rs -> {
                    long compId = rs.getLong("compilation_id");
                    long eventId = rs.getLong("event_id");
                    eventIdsByCompIds.computeIfAbsent(compId, k -> new ArrayList<>())
                            .add(eventId);
                });
        return eventIdsByCompIds;
    }

    private Compilation mapRowToCompilation(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String title = rs.getString("title");
        boolean pinned = rs.getBoolean("pinned");

        return Compilation.builder()
                .id(id)
                .title(title)
                .pinned(pinned)
                .build();
    }

    private void reportNotFound(long id) {
        log.warn("Подборка с id {} не найдена", id);
        throw new NotFoundException(String.format("Подборка с id %d не найдена", id));
    }
}
package ru.practicum.event.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.LocationRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public List<Location> findByIds(List<Long> ids) {
        String sql = "select * from locations where id in (:ids)";
        MapSqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToLocation(rs));
    }

    @Override
    public Location findById(long id) {
        String sql = "select * from locations where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToLocation(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Локация с id {} не найдена", id);
            throw new NotFoundException(String.format("Локация с id %d не найдена", id));
        }
    }

    @Override
    public List<Location> findByLatAndLon(Location location) {
        String sql = "select * from locations where lat = :lat and lon = :lon";
        MapSqlParameterSource parameters = new MapSqlParameterSource("lat", location.getLat());
        parameters.addValue("lon", location.getLon());
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToLocation(rs));
    }

    @Override
    public long add(Location location) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("locations")
                .usingGeneratedKeyColumns("id");
        return simpleJdbcInsert.executeAndReturnKey(location.toMap()).longValue();
    }

    private Location mapRowToLocation(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        float lat = rs.getFloat("lat");
        float lon = rs.getFloat("lon");
        return Location.builder()
                .id(id)
                .lat(lat)
                .lon(lon)
                .build();
    }
}
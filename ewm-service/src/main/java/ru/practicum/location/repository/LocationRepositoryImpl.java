package ru.practicum.location.repository;

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
import ru.practicum.location.enums.LocationType;
import ru.practicum.location.model.Location;
import ru.practicum.location.model.LocationCriteria;
import ru.practicum.location.model.SearchArea;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public List<Location> findNearestByLatAndLon(Location location) {
        String sql = "select * from locations where distance(:lat, :lon, lat, lon) <= radius " +
                "order by radius";
        MapSqlParameterSource parameters = new MapSqlParameterSource("lat", location.getLat());
        parameters.addValue("lon", location.getLon());
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToLocation(rs));
    }

    @Override
    public long add(Location location) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("locations")
                .usingGeneratedKeyColumns("id");
        return simpleJdbcInsert.executeAndReturnKey(location.toFullMap()).longValue();
    }

    @Override
    public long addByUser(Location location) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("locations")
                .usingGeneratedKeyColumns("id", "radius", "type", "name");
        KeyHolder generatedKeys = simpleJdbcInsert.executeAndReturnKeyHolder(location.toMap());
        return (long) Objects.requireNonNull(generatedKeys.getKeys()).get("id");
    }

    @Override
    public List<Location> getByCriteria(LocationCriteria criteria) {
        StringBuilder sql = new StringBuilder("select * from locations");
        List<String> conditions = new ArrayList<>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (criteria.getSearchArea() != null) {
            SearchArea searchArea = criteria.getSearchArea();
            conditions.add("distance(:lat, :lon, lat, lon) <= radius + :radius");
            parameters.addValue("lat", searchArea.getLat());
            parameters.addValue("lon", searchArea.getLon());
            parameters.addValue("radius", searchArea.getRadius());
        }

        if (criteria.getType() != null) {
            conditions.add("type = :type");
            parameters.addValue("type", criteria.getType());
        }

        if (!conditions.isEmpty()) {
            String allConditions = conditions.stream()
                    .collect(Collectors.joining(" AND ", "(", ")"));
            sql.append(" where ").append(allConditions);
        }

        sql.append(" limit :size offset :from");
        parameters.addValue("size", criteria.getSize());
        parameters.addValue("from", criteria.getFrom());

        return namedJdbcTemplate.query(sql.toString(), parameters, (rs, rowNum) -> mapRowToLocation(rs));
    }

    @Override
    public void delete(long id) {
        String sql = "delete from locations where id = ?";
        if (jdbcTemplate.update(sql, id) <= 0) {
            log.warn("Локация с id {} не найдена", id);
            throw new NotFoundException(String.format("Локация с id %d не найдена", id));
        }
    }

    @Override
    public Location update(Location location) {
        String sql = "update locations " +
                "set lat = :lat, lon = :lon, radius = :radius, name = :name, type = :type " +
                "where id = :id";
        MapSqlParameterSource parameters = makeParameterMap(location);
        if (namedJdbcTemplate.update(sql, parameters) > 0) {
            return location;
        }
        log.warn("Локация с id {} не найдена", location.getId());
        throw new NotFoundException(String.format("Локация с id %d не найдена", location.getId()));
    }

    private MapSqlParameterSource makeParameterMap(Location location) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", location.getId());
        parameters.addValue("lat", location.getLat());
        parameters.addValue("lon", location.getLon());
        parameters.addValue("radius", location.getRadius());
        parameters.addValue("type", location.getType().toString());
        parameters.addValue("name", location.getName());
        return parameters;
    }

    private Location mapRowToLocation(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        float lat = rs.getFloat("lat");
        float lon = rs.getFloat("lon");
        int radius = rs.getInt("radius");
        String name = rs.getString("name");
        String stringType = rs.getString("type");
        LocationType type = LocationType.from(stringType)
                .orElseThrow(() -> new ConflictException(
                        String.format("В базу попало что-то не то: %s", stringType)));
        return Location.builder()
                .id(id)
                .lat(lat)
                .lon(lon)
                .radius(radius)
                .name(name)
                .type(type)
                .build();
    }
}
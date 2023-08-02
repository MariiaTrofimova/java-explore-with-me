package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.model.App;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AppRepositoryImpl implements AppRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public Optional<App> findByAppAndUri(String app, String uri) {
        return Optional.empty();
    }

    @Override
    public long add(App app) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("app")
                .usingGeneratedKeyColumns("id");
        return simpleJdbcInsert.executeAndReturnKey(app.toMap()).longValue();
    }

    @Override
    public List<App> getAppsByUris(String[] uris) {
        String sql = "select id from apps where uri in :uris";
        SqlParameterSource parameters = new MapSqlParameterSource("uris", uris);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToApp(rs));
    }

    @Override
    public List<App> getAppsByIds(List<Long> ids) {
        String sql = "";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToApp(rs));
    }

    private App mapRowToApp(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String uri = rs.getString("uri");
        return App.builder()
                .id(id)
                .name(name)
                .uri(uri)
                .build();
    }
}

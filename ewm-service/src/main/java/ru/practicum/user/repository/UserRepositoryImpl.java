package ru.practicum.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public List<User> findByIds(List<Long> ids) {
        String sql = "select * from users where id in (:ids)";
        MapSqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToUser(rs));
    }

    @Override
    public User findById(long id) {
        String sql = "select * from users where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToUser(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    @Override
    public User add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(id);
        return user;
    }

    @Override
    public List<User> findByIds(List<Long> ids, int from, int size) {
        String sql = "select * from users where id in (:ids) order by id limit :size offset :from";
        MapSqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        parameters.addValue("from", from);
        parameters.addValue("size", size);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToUser(rs));
    }

    @Override
    public List<User> get(int from, int size) {
        String sql = "select * from users order by id limit :size offset :from";
        MapSqlParameterSource parameters = new MapSqlParameterSource("from", from);
        parameters.addValue("size", size);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToUser(rs));
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from users where id = ?";
        if (jdbcTemplate.update(sql, id) < 0) {
            log.warn("Пользователь с id {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }
}
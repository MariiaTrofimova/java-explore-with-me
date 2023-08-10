package ru.practicum.category.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.category.Category;
import ru.practicum.error.exceptions.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public List<Category> getAll(int from, int size) {
        String sql = "select * from categories order by id limit :size offset :from";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("from", from);
        parameters.addValue("size", size);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToCategory(rs));
    }

    @Override
    public List<Category> findByIds(List<Long> ids) {
        String sql = "select id, name from categories a where id in (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        return namedJdbcTemplate.query(sql, parameters, (rs, rowNum) -> mapRowToCategory(rs));
    }

    @Override
    public Category findById(Long id) {
        String sql = "select * from categories where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRowToCategory(rs), id);
        } catch (DataRetrievalFailureException e) {
            log.warn("Категория с id {} не найдена", id);
            throw new NotFoundException(String.format("Категория с id %d не найдена", id));
        }
    }

    @Override
    public Category add(Category category) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("categories")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(category.toMap()).longValue();
        category.setId(id);
        return category;
    }

    @Override
    public Category update(Category category) {
        String sql = "update categories set name = ? where id = ?";
        if (jdbcTemplate.update(sql, category.getName(), category.getId()) > 0) {
            return category;
        }
        log.warn("Категория с id {} не найдена", category.getId());
        throw new NotFoundException(String.format("Категория с id %d не найдена", category.getId()));
    }

    @Override
    public void delete(long id) {
        String sql = "delete from categories where id = ?";
        if (jdbcTemplate.update(sql, id) < 0) {
            log.warn("Категория с id {} не найдена", id);
            throw new NotFoundException(String.format("Категория с id %d не найдена", id));
        }
    }

    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        return Category.builder()
                .id(id)
                .name(name)
                .build();
    }
}

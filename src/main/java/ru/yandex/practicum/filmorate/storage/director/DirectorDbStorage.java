package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.Mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DirectorDbStorage implements DirectorDaoStorage {
    private final JdbcTemplate jdbcTemplate;

    private final Mappers mappers;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate, Mappers mappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.mappers = mappers;
    }

    @Override
    public List<Director> getAll() {
        final String sql = "SELECT * FROM director";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeDirector(rs));
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", director.getName());
        director.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());

        return director;
    }

    @Override
    public void delete(Long id) {
        final String sql = "DELETE FROM director WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE director SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());

        return director;
    }

    @Override
    public Optional<Director> getById(Long id) {
        final String sql = "SELECT * FROM director WHERE id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeDirector(rs), id)
                .stream()
                .findAny();
    }

    @Override
    public List<Long> findFilmsByDirector(Long directorId, String sortBy) {
        String sqlByLikes = "SELECT fd.film_id, COUNT(fl.user_id) AS p " +
                "FROM film_directors AS fd " +
                "LEFT OUTER JOIN user_likes_film AS fl ON fd.film_id = fl.film_id " +
                "WHERE director_id = ? " +
                "GROUP BY fd.film_id " +
                "ORDER BY p ";
        String sqlByYear = "SELECT f.id AS film_id FROM film AS f " +
                "INNER JOIN film_directors AS fd ON f.id = fd.film_id AND fd.director_id = ? " +
                "ORDER BY f.release_date";
        if (sortBy.equals("year")) {

            return jdbcTemplate.query(sqlByYear,
                    (rs, rowNum) -> rs.getLong("film_id"), directorId);
        } else {

            return jdbcTemplate.query(sqlByLikes,
                    (rs, rowNum) -> rs.getLong("film_id"), directorId);
        }
    }
}

package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class LikesDbStorage implements LikesDaoStorage{
    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeToFilm(Long filmId, Long userId) {
        final String sql = "INSERT INTO user_likes_film (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void deleteLikeOfFilm(Long filmId, Long userId) {
        final String sql = "DELETE FROM user_likes_film WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public List<Film> getListOfMostPopularFilm(Integer count) {
        final String sql = "SELECT f.*, m.* FROM film AS f LEFT JOIN user_likes_film AS ulf ON f.id = ulf.film_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id" +
                " GROUP BY f.id ORDER BY COUNT(ulf.user_id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa_name")).build())
                .build();
    }
}

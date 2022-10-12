package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LikesDbStorage implements LikesDaoStorage{
    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeToFilm(Long filmId, Long userId) {
        final String sql = "MERGE INTO user_likes_film (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void deleteLikeOfFilm(Long filmId, Long userId) {
        final String sql = "DELETE FROM user_likes_film WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public List<Long> getListOfMostPopularFilm(Integer count, Integer genreId, Integer year) {
        if (genreId == 0 && year == 0) {
            String sql = "SELECT f.id, COUNT(ulf.user_id) as l FROM film AS f " +
                    " LEFT JOIN user_likes_film AS ulf ON f.id = ulf.film_id " +
                    " GROUP BY f.id ORDER BY COUNT(ulf.user_id) DESC LIMIT ?";

            return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), count);
        } else if (genreId != 0 && year != 0) {
            String sql = "SELECT f.id, COUNT(ulf.user_id) as l FROM film AS f " +
                    " LEFT JOIN user_likes_film AS ulf ON f.id = ulf.film_id " +
                    " INNER JOIN film_genre as fg ON f.id = fg.film_id " +
                    " AND fg.genre_id = ? AND YEAR(f.release_date) = ? " +
                    " GROUP BY f.id ORDER BY COUNT(ulf.user_id) DESC LIMIT ?";

            return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), genreId, year, count);
        } else if (genreId != 0) {
            String sql = "SELECT f.id, COUNT(ulf.user_id) as l FROM film AS f " +
                    " LEFT JOIN user_likes_film AS ulf ON f.id = ulf.film_id " +
                    " INNER JOIN film_genre as fg ON f.id = fg.film_id " +
                    " AND fg.genre_id = ? " +
                    " GROUP BY f.id ORDER BY COUNT(ulf.user_id) DESC LIMIT ?";

            return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), genreId, count);
        } else {
            String sql = "SELECT f.id, COUNT(ulf.user_id) as l FROM film AS f " +
                    " LEFT JOIN user_likes_film AS ulf ON f.id = ulf.film_id " +
                    " WHERE YEAR(f.release_date) = ? " +
                    " GROUP BY f.id ORDER BY COUNT(ulf.user_id) DESC LIMIT ?";

            return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), year, count);
        }
    }
}

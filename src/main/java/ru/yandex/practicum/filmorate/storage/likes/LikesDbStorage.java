package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class LikesDbStorage implements LikesDaoStorage{
    private final JdbcTemplate jdbcTemplate;
    private final Mappers mappers;

    public LikesDbStorage(JdbcTemplate jdbcTemplate, Mappers mappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.mappers = mappers;
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
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeFilm(rs), count);
        for (Film film : films) {
            film.setGenres(loadGenresByFilm(film.getId()));
        }
        return films;
    }

    private Set<Genre> loadGenresByFilm(Long id) {
        final String sqlGenre = "SELECT * FROM genre AS g LEFT JOIN film_genre AS fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlGenre, (res, rowNum) -> mappers.makeGenre(res), id);
        return new HashSet<>(genres);
    }
}

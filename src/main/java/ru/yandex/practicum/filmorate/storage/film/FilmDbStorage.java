package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Mappers;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final Mappers mappers;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, Mappers mappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.mappers = mappers;
    }

    @Override
    public List<Film> getAll() {
        final String sql = "SELECT * FROM film AS f LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeFilm(rs));
        for (Film film : films) {
            film.setGenres(loadGenresByFilm(film.getId()));
            film.setDirectors(loadDirectorsByFilm(film.getId()));
        }
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        if (film == null) {
            throw new NotFoundException("Невозможно создать фильм. Передано пустое значение фильма.");
        }
        makeFilm(film);
        setGenreByFilm(film);
        setDirectorByFilm(film);
        return film;
    }

    private void makeFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO film (name, description, duration, release_date, mpa_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ID"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setInt(3, film.getDuration());
            statement.setDate(4, Date.valueOf(film.getReleaseDate()));
            statement.setLong(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
    }

    @Override
    public void deleteFilm(Long id) {
        final String sql = "DELETE FROM film WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Film updateFilm(Film film) {
        final String sql = "UPDATE film SET name = ?, description = ?, duration = ?, release_date = ?, " +
                "MPA_ID = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(),
                film.getMpa().getId(), film.getId());
        setGenreByFilm(film);
        setDirectorByFilm(film);
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        final String sql = "SELECT * FROM film AS f LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id WHERE f.id = ?";
        Film film = jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeFilm(rs), id)
                .stream()
                .findAny().orElse(null);
        if (film != null) {
            film.setGenres(loadGenresByFilm(id));
            film.setDirectors(loadDirectorsByFilm(id));
        }
        return film;
    }

    @Override
    public List<Film> getRecommendationsFilm(Long id) {
        List<Film> recommendationFilms = new ArrayList<>();
        final String sql = "SELECT ul2.user_id FROM user_likes_film AS ul1 " +
                "JOIN user_likes_film AS ul2 ON ul1.film_id = ul2.film_id " +
                "WHERE ul1.user_id <> ul2.user_id AND ul1.user_id = ? " +
                "GROUP BY ul2.user_id ORDER BY COUNT (ul2.film_id) DESC LIMIT 1";
        final List<Long> sameLikeUser = jdbcTemplate.queryForList(sql, Long.class, id);
        if (sameLikeUser.size() != 1) {
            return recommendationFilms;
        }
        final String sqlRecommend = "SELECT f.*, m.* FROM film AS f LEFT JOIN user_likes_film AS ulf ON f.id = ulf.film_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE ulf.user_id = ? AND ulf.film_id NOT IN (SELECT film_id FROM user_likes_film WHERE user_id = ?)";
        recommendationFilms = jdbcTemplate.query(sqlRecommend, (rs, rowNum) -> mappers.makeFilm(rs), sameLikeUser.get(0), id);
        for (Film film : recommendationFilms) {
            film.setGenres(loadGenresByFilm(film.getId()));
        }
        return recommendationFilms;
    }

    @Override
    public List<Film> getCommonFilms(Long id, Long friendId) {
        final String sql = "SELECT f.*, m.* FROM film AS f LEFT JOIN user_likes_film AS ulf ON f.id = ulf.film_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE ulf.user_id = ? AND ulf.film_id IN (SELECT film_id FROM user_likes_film WHERE user_id = ?)" +
                " GROUP BY f.id ORDER BY COUNT(ulf.user_id) DESC";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeFilm(rs), id, friendId);
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
    
    private void setGenreByFilm(Film film) {
        String sqlUpdateGenre = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlUpdateGenre, film.getId());
        String sqlGenre = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        Set<Genre> genres = film.getGenres();
        if (genres == null) {
            return;
        }
        for (Genre genre : genres) {
            jdbcTemplate.update(sqlGenre, film.getId(), genre.getId());
        }
    }

    private Set<Director> loadDirectorsByFilm(Long id) {
        final String sql = "SELECT * FROM director AS d INNER JOIN film_directors AS fd ON d.id = fd.director_id " +
                "AND fd.film_id = ?";
        List<Director> directors = jdbcTemplate.query(sql, (res, rowNum) -> mappers.makeDirector(res), id);
        return new HashSet<>(directors);
    }

    private void setDirectorByFilm(Film film) {
        String sqlUpdateGenre = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sqlUpdateGenre, film.getId());
        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
        Set<Director> directors = film.getDirectors();
        if (directors == null) {
            return;
        }
        for (Director director : directors) {
            jdbcTemplate.update(sql, film.getId(), director.getId());
        }
    }

    @Override
    public List<Film> searchFilmByTitle(String query) {
        String sqlSearchFilm = "SELECT f.*, m.* FROM film AS f LEFT JOIN mpa AS m ON f.mpa_id=m.mpa_id " +
                "LEFT JOIN film_directors AS fd ON f.id=fd.film_id LEFT JOIN director AS d ON d.id=fd.director_id " +
                "LEFT JOIN user_likes_film AS ulf ON f.id=ulf.film_id WHERE f.name iLIKE CONCAT('%', ?, '%') " +
                "GROUP BY f.id ORDER BY COUNT(ulf.user_id) DESC;";
        List<Film> films = jdbcTemplate.query(sqlSearchFilm, (rs, rowNum) -> mappers.makeFilm(rs), query);
        for (Film film : films) {
            film.setDirectors(loadDirectorsByFilm(film.getId()));
            film.setGenres(loadGenresByFilm(film.getId()));
        }
        return films;
    }

    @Override
    public List<Film> searchFilmByDirect(String query) {
        String sqlSearchFilm = "SELECT f.*, m.* FROM film AS f LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_directors AS fd ON f.id=fd.film_id LEFT JOIN director AS d ON d.id=fd.director_id " +
                "LEFT JOIN user_likes_film AS ulf ON f.id=ulf.film_id WHERE d.name iLIKE CONCAT('%', ?, '%') " +
                "GROUP BY f.id ORDER BY COUNT(ulf.user_id) DESC;";
        List<Film> films = jdbcTemplate.query(sqlSearchFilm, (rs, rowNum) -> mappers.makeFilm(rs), query);
        for (Film film : films) {
            film.setDirectors(loadDirectorsByFilm(film.getId()));
            film.setGenres(loadGenresByFilm(film.getId()));
        }
        return films;
    }

    @Override
    public List<Film> searchFilmByTitleAndDirect(String query) {
        String sqlSearchFilm = "SELECT f.*, m.* FROM film AS f LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_directors AS fd ON f.id=fd.film_id LEFT JOIN director AS d ON d.id=fd.director_id " +
                "LEFT JOIN USER_LIKES_FILM AS ulf ON f.id=ulf.film_id WHERE (d.name iLIKE CONCAT('%', ?, '%')) " +
                "OR (f.name ILIKE CONCAT('%', ?, '%')) GROUP BY f.id ORDER BY COUNT(ulf.user_id) DESC;";
        List<Film> films = jdbcTemplate.query(sqlSearchFilm, (rs, rowNum) -> mappers.makeFilm(rs), query, query);
        for (Film film : films) {
            film.setDirectors(loadDirectorsByFilm(film.getId()));
            film.setGenres(loadGenresByFilm(film.getId()));
        }
        return films;
    }
}

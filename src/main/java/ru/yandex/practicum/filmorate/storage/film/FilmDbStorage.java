package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Mappers;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage{
    private final static LocalDate DATE_OF_FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);
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
            film.setDirector(loadDirectorsByFilm(film.getId()));
        }
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        validation(film);
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
        validation(film);
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
            film.setDirector(loadDirectorsByFilm(id));
        }
        return film;
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
        Set<Director> directors = film.getDirector();
        if (directors == null) {
            return;
        }
        for (Director director : directors) {
            jdbcTemplate.update(sql, film.getId(), director.getId());
        }
    }

    public void validation(@Valid @RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("При попытке создать или обновить фильм произошла ошибка названия фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("При попытке создать или обновить фильм произошла ошибка описания фильма");
            throw new ValidationException("Длина описания фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(DATE_OF_FIRST_FILM_RELEASE)) {
            log.error("При попытке создать или обновить фильм произошла ошибка даты релиза фильма");
            throw new ValidationException("Дата релиза фильма не может быть раньше 28.12.1895");
        }
        if (film.getDuration() < 0) {
            log.error("При попытке создать или обновить фильм произошла ошибка продолжительности фильма");
            throw new ValidationException("Продолжительность фильма не может быть отрицательным значением");
        }
    }
}

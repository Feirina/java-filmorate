package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Long, Film> films = new HashMap<>();
    private Long countOfFilmId = 1L;
    private final static LocalDate DATE_OF_FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        validationFilm(film);
        log.info("Фильм {} сохранен", film);
        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        if (!films.containsKey(id)) {
            log.error("При попытке удалить фильм возникла ошибка");
            throw new NotFoundException("Фильма с данным id не существует");
        }
        films.remove(id);
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("При попытке обновить данные фильма возникла ошибка");
            throw new NotFoundException("Фильма с данным id не существует");
        }
        validationFilm(film);
        log.info("Фильм {} обновлен", film);
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        if (!films.containsKey(id)) {
            log.error("При попытке получить данные фильма возникла ошибка");
            throw new NotFoundException("Фильма с данным id не существует");
        }
        log.info("Данные фильма {} получены", films.get(id));
        return films.get(id);
    }

    private void validationFilm(@Valid @RequestBody Film film) {
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
        if (film.getId() == null || film.getId() == 0) {
            film.setId(countOfFilmId);
            countOfFilmId++;
        }
        films.put(film.getId(), film);
    }
}

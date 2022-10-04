package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ComparatorForFilms;
import ru.yandex.practicum.filmorate.storage.Storage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage extends Storage<Film> implements FilmStorage{
    ComparatorForFilms comparator = new ComparatorForFilms();
    private final Map<Long, Film> films = new HashMap<>();
    private Long countOfFilmId = 1L;
    private static final LocalDate DATE_OF_FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    @Override
    public Map<Long, Film> getMap() {
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        return create(film);
    }

    @Override
    public void deleteFilm(Long id) {
        delete(id);
    }

    @Override
    public Film updateFilm(Film film) {
        return update(film);
    }

    @Override
    public Film getFilm(Long id) {
        log.info("Данные фильма {} получены", films.get(id));
        return get(id);
    }

    @Override
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
        if (film.getId() == null || film.getId() == 0) {
            film.setId(countOfFilmId);
            countOfFilmId++;
        }
        films.put(film.getId(), film);
    }

    @Override
    public List<Film> searchFilmByTitle(String query, List<String> by) {
        List<Film> films = getAll();
        films.sort(comparator);
        films.removeIf(film -> !film.getName().contains(query));
        log.info("Количество найденных фильмов: {}", films.size());
            return new ArrayList<>(films);
    }
}

package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage extends Storage<Film> implements FilmStorage {
    ComparatorForFilms comparator = new ComparatorForFilms();

    private final Map<Long, Film> films = new HashMap<>();

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
    public List<Film> getRecommendationsFilm(Long id) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(Long id, Long friendId) {
        return null;
    }

    @Override
    public List<Film> searchFilmByTitle(String query) {
        List<Film> films = getAll();
        films.sort(comparator);
        films.removeIf(film -> !film.getName().contains(query));
        log.info("Количество найденных фильмов: {}", films.size());

        return new ArrayList<>(films);
    }

    @Override
    public List<Film> searchFilmByDirect(String query) {
        return null;
    }

    @Override
    public List<Film> searchFilmByTitleAndDirect(String query) {
        return null;
    }
}

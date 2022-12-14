package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;

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
    public Film create(Film film) {
        return create(film);
    }

    @Override
    public void delete(Long id) {
        delete(id);
    }

    @Override
    public Film update(Film film) {
        return update(film);
    }

    @Override
    public Optional<Film> getById(Long id) {
        log.info("Данные фильма {} получены", films.get(id));

        return Optional.ofNullable(get(id));
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

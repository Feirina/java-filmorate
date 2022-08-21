package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService implements FilmStorage {
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        if (!film.getUsersIdsOfLikes().contains(userId)) {
            film.setCountOfLike(film.getCountOfLike() + 1);
            film.getUsersIdsOfLikes().add(userId);
        }
    }

    public void deleteLikeOfFilm(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        if (!film.getUsersIdsOfLikes().contains(userId)) {
            throw new NotFoundException("Невозможно удалить лайк - пользователь с данным userId не ставил лайк");
        }
        film.getUsersIdsOfLikes().remove(userId);
        film.setCountOfLike(film.getCountOfLike() - 1);
    }

    public List<Film> getListOfMostPopularFilm(Integer count) {
        return inMemoryFilmStorage.getFilms().values().stream().sorted().limit(count).collect(Collectors.toList());
    }

    @Override
    public List<Film> getAll() {
        return inMemoryFilmStorage.getAll();
    }

    @Override
    public Film createFilm(Film film) {
        return inMemoryFilmStorage.createFilm(film);
    }

    @Override
    public void deleteFilm(Long id) {
        inMemoryFilmStorage.deleteFilm(id);
    }

    @Override
    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    @Override
    public Film getFilm(Long id) {
        return inMemoryFilmStorage.getFilm(id);
    }
}

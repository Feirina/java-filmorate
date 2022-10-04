package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDaoStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesDaoStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService implements FilmorateService<Film> {
    private final FilmStorage filmStorage;
    private final LikesDaoStorage likesStorage;
    private final UserStorage userStorage;
    private final DirectorDaoStorage directorStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       LikesDaoStorage likesStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       DirectorDaoStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Невозможно добавить лайк фильма с данным id не существует");
        } else if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Невозможно добавить лайк пользователя с данным id не существует");
        }
        likesStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLikeOfFilm(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Невозможно удалить лайк фильма с данным id не существует");
        } else if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Невозможно удалить лайк пользователя с данным id не существует");
        }
        likesStorage.deleteLikeOfFilm(filmId, userId);
    }

    public List<Film> getListOfMostPopularFilm(Integer count) {
        return likesStorage.getListOfMostPopularFilm(count);
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public void deleteFilm(Long id) {
        if (filmStorage.getFilm(id) == null) {
            throw new NotFoundException("Фильма с данным id не существует");
        }
        filmStorage.deleteFilm(id);
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilm(film.getId()) == null) {
            throw new NotFoundException("Фильма с данным id не существует");
        }
        return filmStorage.updateFilm(film);
    }

    @Override
    public Film getById(Long id) {
        final Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильма с данным id не существует");
        }
        return film;
    }

    public List<Film> findFilmsByDirector(Long directorId, String sortBy) {
        List<Film> films = new ArrayList<>();
        List<Long> filmsId = directorStorage.findFilmsByDirector(directorId, sortBy);
        if (filmsId.isEmpty()) throw new NotFoundException("");
        for (Long id : filmsId) {
            films.add(getById(id));
        }
        return films;
    }

}

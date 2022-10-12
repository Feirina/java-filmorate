package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorDaoStorage;
import ru.yandex.practicum.filmorate.storage.event.EventDaoStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesDaoStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.yandex.practicum.filmorate.model.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.Operation.REMOVE;

@Slf4j
@Service
public class FilmService implements FilmorateService<Film> {
    private static final LocalDate DATE_OF_FIRST_FILM_RELEASE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;

    private final LikesDaoStorage likesStorage;

    private final UserStorage userStorage;

    private final DirectorDaoStorage directorStorage;

    private final EventDaoStorage eventStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       LikesDaoStorage likesStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       DirectorDaoStorage directorStorage,
                       EventDaoStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
        this.eventStorage = eventStorage;
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Невозможно добавить лайк фильма с данным id не существует");
        } else if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Невозможно добавить лайк пользователя с данным id не существует");
        }
        likesStorage.addLikeToFilm(filmId, userId);
        eventStorage.fixEvent(userId, filmId, LIKE, ADD);
    }

    public void deleteLikeOfFilm(Long filmId, Long userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new NotFoundException("Невозможно удалить лайк фильма с данным id не существует");
        } else if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Невозможно удалить лайк пользователя с данным id не существует");
        }
        likesStorage.deleteLikeOfFilm(filmId, userId);
        eventStorage.fixEvent(userId, filmId, LIKE, REMOVE);
    }

    public List<Film> getListOfMostPopularFilm(Integer count, Integer genreId, Integer year) {
        List<Film> films = new ArrayList<>();
        List<Long> filmsId = likesStorage.getListOfMostPopularFilm(count, genreId, year);
        for (Long filmId : filmsId) {
            films.add(getById(filmId));
        }

        return films;
    }

    public List<Film> getCommonFilms(Long id, Long friendId) {
        if (userStorage.getUser(id) == null || userStorage.getUser(friendId) == null) {
            throw new NotFoundException("Невозможно получить список общих фильмов - пользователя с данным id не существует");
        }

        return filmStorage.getCommonFilms(id, friendId);
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        if (film == null) {
            throw new NotFoundException("Невозможно создать фильм. Передано пустое значение фильма.");
        }
        throwIfFilmDateNotValid(film);

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
        throwIfFilmDateNotValid(film);

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
        if (directorStorage.getDirector(directorId) == null) {
            throw new NotFoundException("Режиссера с данным id не существует");
        }
        List<Film> films = new ArrayList<>();
        List<Long> filmsId = directorStorage.findFilmsByDirector(directorId, sortBy);
        for (Long id : filmsId) {
            films.add(getById(id));
        }

        return films;
    }

    public List<Film> searchFilm(String query, List<String> by) {
        if (by.contains("title") && by.size() == 1) {

            return filmStorage.searchFilmByTitle(query);
        } else if (by.contains("director") && by.size() == 1) {

            return filmStorage.searchFilmByDirect(query);
        } else if (by.contains("title") && by.contains("director") && by.size() == 2) {

            return filmStorage.searchFilmByTitleAndDirect(query);
        }

        return Collections.emptyList();
    }

    private void throwIfFilmDateNotValid(Film film) {
        if (film.getReleaseDate().isBefore(DATE_OF_FIRST_FILM_RELEASE)) {
            log.error("При попытке создать или обновить фильм произошла ошибка даты релиза фильма");
            throw new BadRequestException("Дата релиза фильма не может быть раньше 28.12.1895");
        }
    }
}

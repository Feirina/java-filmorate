package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService { //Это я запуталась из-за обсуждений в слаке, увидела у кого-то имплементацию
                           //FilmStorage и слово "зависимость" в ТЗ и почему-то решила, что надо делать так >_<
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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
        return filmStorage.getMap()
                .values()
                .stream()
                .sorted()
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public void deleteFilm(Long id) {
        if (!filmStorage.getMap().containsKey(id)) {
            log.error("При попытке удалить фильм возникла ошибка");
            throw new NotFoundException("Фильма с данным id не существует");
        }
        filmStorage.deleteFilm(id);
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.getMap().containsKey(film.getId())) {
            log.error("При попытке обновить данные фильма возникла ошибка");
            throw new NotFoundException("Фильма с данным id не существует");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long id) {
        if (!filmStorage.getMap().containsKey(id)) {
            log.error("При попытке получить данные фильма возникла ошибка");
            throw new NotFoundException("Фильма с данным id не существует");
        }
        return filmStorage.getFilm(id);
    }
}

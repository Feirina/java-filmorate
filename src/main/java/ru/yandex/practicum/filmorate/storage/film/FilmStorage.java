package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();
    Film createFilm(Film film);
    void deleteFilm(Long id);
    Film updateFilm(Film film);
    Film getFilm(Long id);
}

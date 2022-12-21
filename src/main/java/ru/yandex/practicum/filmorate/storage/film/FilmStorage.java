package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.common.CRUD;
import ru.yandex.practicum.filmorate.common.Filmorate;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends Filmorate<Film>, CRUD<Film> {
    List<Film> getRecommendationsFilm(Long id);

    List<Film> getCommonFilms(Long id, Long friendId);

    List<Film> searchFilmByTitle(String query);

    List<Film> searchFilmByDirect(String query);

    List<Film> searchFilmByTitleAndDirect(String query);
}

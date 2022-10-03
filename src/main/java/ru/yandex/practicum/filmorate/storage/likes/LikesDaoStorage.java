package ru.yandex.practicum.filmorate.storage.likes;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesDaoStorage {
    void addLikeToFilm(Long filmId, Long userId);
    void deleteLikeOfFilm(Long filmId, Long userId);
    List<Long> getListOfMostPopularFilm(Integer count, Integer genreId, Integer year);
}

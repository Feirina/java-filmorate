package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.common.CRUD;
import ru.yandex.practicum.filmorate.common.Filmorate;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDaoStorage extends Filmorate<Director>, CRUD<Director> {
    List<Long> findFilmsByDirector(Long directorId, String sortBy);
}

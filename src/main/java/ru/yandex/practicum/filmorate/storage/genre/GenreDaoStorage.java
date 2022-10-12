package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.common.Filmorate;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDaoStorage extends Filmorate<Genre> {
    List<Genre> getAll();

    Genre getById(Long id);
}

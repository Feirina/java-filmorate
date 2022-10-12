package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.common.Filmorate;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDaoStorage extends Filmorate<Genre> {
    List<Genre> getAll();

    Optional<Genre> getById(Long id);
}

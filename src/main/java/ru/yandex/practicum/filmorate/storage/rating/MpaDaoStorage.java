package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.common.Filmorate;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaDaoStorage extends Filmorate<Mpa> {
    List<Mpa> getAll();

    Optional<Mpa> getById(Long id);
}

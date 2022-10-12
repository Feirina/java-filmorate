package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.common.Filmorate;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDaoStorage extends Filmorate<Mpa> {
    List<Mpa> getAll();

    Mpa getById(Long id);
}

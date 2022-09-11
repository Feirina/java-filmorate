package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDaoStorage {
    List<Mpa> getAllRating();
    Mpa getRatingById(Integer id);
}

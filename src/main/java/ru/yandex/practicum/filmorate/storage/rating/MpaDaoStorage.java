package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaDaoStorage {
    List<Mpa> getAllRating();
    Optional<Mpa> getRatingById(Long id);
}

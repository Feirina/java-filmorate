package ru.yandex.practicum.filmorate.common;

import java.util.List;
import java.util.Optional;

public interface Filmorate<T> {
    List<T> getAll();

    Optional<T> getById(Long id);
}

package ru.yandex.practicum.filmorate.common;

import java.util.List;

public interface Filmorate<T> {
    List<T> getAll();

    T getById(Long id);
}

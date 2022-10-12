package ru.yandex.practicum.filmorate.common;

public interface CRUD<T> {
    T create(T entity);

    T update(T entity);

    void delete(Long id);
}

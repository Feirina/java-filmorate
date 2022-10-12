package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.Filmorate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaDaoStorage;

import java.util.List;

@Service
public class MpaService implements Filmorate<Mpa> {
    private final MpaDaoStorage ratingStorage;

    public MpaService(MpaDaoStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    @Override
    public List<Mpa> getAll() {
        return ratingStorage.getAll();
    }

    @Override
    public Mpa getById(Long id) {
        final Mpa mpa = ratingStorage.getById(id);
        if (mpa == null) {
            throw new NotFoundException("Рейтинга с данным id не существует");
        }

        return mpa;
    }
}

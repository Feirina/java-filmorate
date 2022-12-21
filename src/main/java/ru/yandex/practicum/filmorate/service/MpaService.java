package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaDaoStorage;

import java.util.List;

@Service
public class MpaService implements FilmorateService<Mpa> {
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
        return ratingStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинга с данным id не существует"));
    }
}

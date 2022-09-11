package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaDaoStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaDaoStorage ratingStorage;

    public MpaService(MpaDaoStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<Mpa> getAllRating() {
        return ratingStorage.getAllRating();
    }

    public Mpa getRatingById(Integer id) {
        final Mpa mpa = ratingStorage.getRatingById(id);
        if (mpa == null) throw new NotFoundException("Рейтинга с данным id не существует");
        return mpa;
    }
}

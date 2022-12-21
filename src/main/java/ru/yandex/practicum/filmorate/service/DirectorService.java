package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.CRUD;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDaoStorage;

import java.util.List;

@Service
public class DirectorService implements FilmorateService<Director>, CRUD<Director> {
    private final DirectorDaoStorage directorStorage;

    public DirectorService(DirectorDaoStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    @Override
    public Director getById(Long id) {
        return directorStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Режиссера с данным id не существует"));
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public void delete(Long id) {
        if (directorStorage.getById(id).isEmpty()) {
            throw new NotFoundException("Режиссера с данным id не существует");
        }
        directorStorage.delete(id);
    }

    public Director update(Director director) {
        if (directorStorage.getById(director.getId()).isEmpty()) {
            throw new NotFoundException("Режиссера с данным id не существует");
        }

        return directorStorage.update(director);
    }

}

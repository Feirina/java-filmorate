package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.CRUD;
import ru.yandex.practicum.filmorate.common.Filmorate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDaoStorage;

import java.util.List;

@Service
public class DirectorService implements Filmorate<Director>, CRUD<Director> {
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
        Director director = directorStorage.getById(id);
        if (director == null) {
            throw new NotFoundException("Режиссера с данным id не существует");
        }

        return director;
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public void delete(Long id) {
        if (directorStorage.getById(id) == null) {
            throw new NotFoundException("Режиссера с данным id не существует");
        }
        directorStorage.delete(id);
    }

    public Director update(Director director) {
        if (directorStorage.getById(director.getId()) == null) {
            throw new NotFoundException("Режиссера с данным id не существует");
        }

        return directorStorage.update(director);
    }

}

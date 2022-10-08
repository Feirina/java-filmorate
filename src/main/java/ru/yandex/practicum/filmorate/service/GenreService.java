package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDaoStorage;

import java.util.List;

@Service
public class GenreService implements FilmorateService<Genre> {
    private final GenreDaoStorage genreStorage;

    public GenreService(GenreDaoStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAll() {
        return genreStorage.getAllGenre();
    }

    public Genre getById(Long id) {
        Genre genre = genreStorage.getGenreById(id);
        if (genre == null) {
            throw new NotFoundException("Жанра с данным id не существует");
        }
        return genre;
    }
}

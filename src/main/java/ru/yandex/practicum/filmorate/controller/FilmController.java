package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        if (!filmService.getInMemoryFilmStorage().getMap().containsKey(id)) {
            log.error("При попытке получить данные фильма возникла ошибка");
            throw new NotFoundException("Фильма с данным id не существует");
        }
        return filmService.getFilm(id);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        if (!filmService.getInMemoryFilmStorage().getMap().containsKey(id)) {
            log.error("При попытке удалить фильм возникла ошибка");
            throw new NotFoundException("Фильма с данным id не существует");
        }
        filmService.deleteFilm(id);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (!filmService.getInMemoryFilmStorage().getMap().containsKey(film.getId())) {
            log.error("При попытке обновить данные фильма возникла ошибка");
            throw new NotFoundException("Фильма с данным id не существует");
        }
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeOfFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLikeOfFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getListOfMostPopularFilm(count);
    }
}

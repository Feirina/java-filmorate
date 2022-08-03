package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        getFilm(film);
        log.info("Фильм {} сохранен", film);
        return film;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("При попытке обновить данные фильма возникла ошибка");
            throw new ValidationException("Фильма с данным id не существует");
        }
        getFilm(film);
        log.info("Фильм {} обновлен", film);
        return film;
    }

    private void getFilm(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("При попытке создать или обновить фильм произошла ошибка названия фильма");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("При попытке создать или обновить фильм произошла ошибка описания фильма");
            throw new ValidationException("Длина описания фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("При попытке создать или обновить фильм произошла ошибка даты релиза фильма");
            throw new ValidationException("Дата релиза фильма не может быть раньше 28.12.1895");
        }
        if (film.getDuration() < 0) {
            log.error("При попытке создать или обновить фильм произошла ошибка продолжительности фильма");
            throw new ValidationException("Продолжительность фильма не может быть отрицательным значением");
        }
        if (film.getId() == 0) {
            film.setId(1);
        }
        films.put(film.getId(), film);
    }
}

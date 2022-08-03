package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        getUser(user);
        log.info("Пользователь {} сохранен", user);
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("При попытке обновить данные пользователя возникла ошибка");
            throw new ValidationException("Пользователя с данным id не существует");
        }
        getUser(user);
        log.info("Данные пользователя {} обновлены", user);
        return user;
    }

    private void getUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("При создании или обновлении пользователя возникла ошибка при вводе e-mail");
            throw new ValidationException("Введен неккоректный e-mail адрес");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("При создании или обновлении пользователя возникла ошибка при вводе логина");
            throw new ValidationException("Введен неккоректный логин");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("При создании или обновлении пользователя возникла ошибка при вводе даты рождения");
            throw new ValidationException("Введена неккоректная дата рождения");
        }
        if (user.getId() == 0) {
            user.setId(1);
        }
        users.put(user.getId(), user);
    }
}

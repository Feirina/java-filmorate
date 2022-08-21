package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{
    private final Map<Long, User> users = new HashMap<>();
    private Long countOfUserId = 1L;

    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        validationUser(user);
        log.info("Пользователь {} сохранен", user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            log.error("При попытке удалить пользователя возникла ошибка");
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        users.remove(id);
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("При попытке обновить данные пользователя возникла ошибка");
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        validationUser(user);
        log.info("Данные пользователя {} обновлены", user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        if (!users.containsKey(id)) {
            log.error("При попытке получить данные пользователя возникла ошибка");
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        log.info("Данные пользователя {} получены", users.get(id));
        return users.get(id);
    }

    private void validationUser(@RequestBody User user) {
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
        if (user.getId() == null || user.getId() == 0) {
            user.setId(countOfUserId);
            countOfUserId++;
        }
        users.put(user.getId(), user);
    }
}

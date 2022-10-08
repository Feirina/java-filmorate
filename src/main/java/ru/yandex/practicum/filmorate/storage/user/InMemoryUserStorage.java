package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage extends Storage<User> implements UserStorage{
    private final Map<Long, User> users = new HashMap<>();

    private Long countOfUserId = 1L;

    @Override
    public Map<Long, User> getMap() {
        return users;
    }

    @Override
    public User createUser(User user) {
        return create(user);
    }

    @Override
    public void deleteUser(Long id) {
        delete(id);
    }

    @Override
    public User updateUser(User user) {
        return update(user);
    }

    @Override
    public User getUser(Long id) {
        log.info("Данные пользователя {} получены", users.get(id));
        return get(id);
    }

    @Override
    public void validation(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("При создании или обновлении пользователя возникла ошибка при вводе e-mail");
            throw new BadRequestException("Введен неккоректный e-mail адрес");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("При создании или обновлении пользователя возникла ошибка при вводе логина");
            throw new BadRequestException("Введен неккоректный логин");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("При создании или обновлении пользователя возникла ошибка при вводе даты рождения");
            throw new BadRequestException("Введена неккоректная дата рождения");
        }
        if (user.getId() == null || user.getId() == 0) {
            user.setId(countOfUserId);
            countOfUserId++;
        }
        users.put(user.getId(), user);
    }
}
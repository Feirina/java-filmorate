package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.HashMap;
import java.util.Map;

@Component("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage extends Storage<User> implements UserStorage{
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getMap() {
        return users;
    }

    @Override
    public User create(User user) {
        return create(user);
    }

    @Override
    public void delete(Long id) {
        delete(id);
    }

    @Override
    public User update(User user) {
        return update(user);
    }

    @Override
    public User getById(Long id) {
        log.info("Данные пользователя {} получены", users.get(id));

        return get(id);
    }
}
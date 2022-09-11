package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();
    User createUser(User user);
    void deleteUser(Long id);
    User updateUser(User user);
    User getUser(Long id);
}
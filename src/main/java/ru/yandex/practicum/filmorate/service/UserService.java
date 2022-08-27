package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addToFriends(Long id, Long friendId) {
        if (!userStorage.getMap().containsKey(friendId)) {
            throw new NotFoundException("Невозможно добавить в друзья - пользователя с данным friendId не существует");
        }
        getUser(id).getFriends().put(friendId, false);
        getUser(friendId).getFriends().put(id, false);
    }

    public void removeFromFriends(Long id, Long friendId) {
        getUser(id).getFriends().remove(friendId);
        getUser(friendId).getFriends().remove(id);
    }

    public List<User> getListOfMutualFriends(Long id, Long otherId) {
        List<User> listOfMutualFriends = new ArrayList<>();
        for (Long friendId : getUser(id).getFriends().keySet()) {
            if (getUser(otherId).getFriends().containsKey(friendId)) {
                listOfMutualFriends.add(userStorage.getMap().get(friendId));
            }
        }
        return listOfMutualFriends;
    }

    public List<User> getListOfFriends(Long id) {
        List<User> listOfFriends = new ArrayList<>();
        for (Long friendId : getUser(id).getFriends().keySet()) {
            listOfFriends.add(userStorage.getMap().get(friendId));
        }
        return listOfFriends;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public void deleteUser(Long id) {
        if (!userStorage.getMap().containsKey(id)) {
            log.error("При попытке удалить пользователя возникла ошибка");
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        userStorage.deleteUser(id);
    }

    public User updateUser(User user) {
        if (!userStorage.getMap().containsKey(user.getId())) {
            log.error("При попытке обновить данные пользователя возникла ошибка");
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        return userStorage.updateUser(user);
    }

    public User getUser(Long id) {
        if (!userStorage.getMap().containsKey(id)) {
            log.error("При попытке получить данные пользователя возникла ошибка");
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        return userStorage.getUser(id);
    }
}

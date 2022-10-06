package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDaoStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService implements FilmorateService<User> {
    private final UserStorage userStorage;
    private final FriendsDaoStorage friendsStorage;
    private final FilmStorage filmStorage;


    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, FriendsDaoStorage friendsStorage,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.filmStorage = filmStorage;
    }

    public void addToFriends(Long id, Long friendId) {
        if (userStorage.getUser(id) == null || userStorage.getUser(friendId) == null) {
            throw new NotFoundException("Невозможно добавить в друзья - пользователя с данным id не существует");
        }
        friendsStorage.addToFriends(id, friendId);
    }

    public void removeFromFriends(Long id, Long friendId) {
        if (userStorage.getUser(id) == null || userStorage.getUser(friendId) == null) {
            throw new NotFoundException("Невозможно удалить из друзей - пользователя с данным id не существует");
        }
        friendsStorage.removeFromFriends(id, friendId);
    }

    public List<User> getListOfMutualFriends(Long id, Long otherId) {
        if (userStorage.getUser(id) == null || userStorage.getUser(otherId) == null) {
            throw new NotFoundException("Невозможно получить список общих друзей - пользователя с данным id не существует");
        }
        return friendsStorage.getListOfMutualFriends(id, otherId);
    }

    public List<User> getListOfFriends(Long id) {
        if (userStorage.getUser(id) == null) {
            throw new NotFoundException("Невозможно получить список друзей - пользователя с данным id не существует");
        }
        return friendsStorage.getListOfFriends(id);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public void deleteUser(Long id) {
        if (userStorage.getUser(id) == null) {
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        userStorage.deleteUser(id);
    }

    public User updateUser(User user) {
        if (userStorage.getUser(user.getId()) == null) {
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        return userStorage.updateUser(user);
    }

    @Override
    public User getById(Long id) {
        final User user = userStorage.getUser(id);
        if (user == null) {
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        return user;
    }

    public List<Film> getRecommendationsFilm(Long id) {
        if (userStorage.getUser(id) == null) {
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        return filmStorage.getRecommendationsFilm(id);
    }
}

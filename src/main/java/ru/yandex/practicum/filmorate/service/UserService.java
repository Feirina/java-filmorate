package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.event.EventDaoStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDaoStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService implements FilmorateService<User> {
    private final UserStorage userStorage;

    private final FriendsDaoStorage friendsStorage;

    private final EventDaoStorage eventStorage;

    private final FilmStorage filmStorage;


    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       FriendsDaoStorage friendsStorage,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       EventDaoStorage eventStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
    }

    public void addToFriends(Long id, Long friendId) {
        if (userStorage.getUser(id).isEmpty() || userStorage.getUser(friendId).isEmpty()) {
            throw new NotFoundException("Невозможно добавить в друзья - пользователя с данным id не существует");
        }
        friendsStorage.addToFriends(id, friendId);
        eventStorage.fixEvent(id, friendId, EventType.FRIEND, Operation.ADD);
    }

    public void removeFromFriends(Long id, Long friendId) {
        if (userStorage.getUser(id).isEmpty() || userStorage.getUser(friendId).isEmpty()) {
            throw new NotFoundException("Невозможно удалить из друзей - пользователя с данным id не существует");
        }
        friendsStorage.removeFromFriends(id, friendId);
        eventStorage.fixEvent(id, friendId, EventType.FRIEND, Operation.REMOVE);
    }

    public List<User> getListOfMutualFriends(Long id, Long otherId) {
        if (userStorage.getUser(id).isEmpty() || userStorage.getUser(otherId).isEmpty()) {
            throw new NotFoundException("Невозможно получить список общих друзей - пользователя с данным id не существует");
        }

        return friendsStorage.getListOfMutualFriends(id, otherId);
    }

    public List<User> getListOfFriends(Long id) {
        if (userStorage.getUser(id).isEmpty()) {
            throw new NotFoundException("Невозможно получить список друзей - пользователя с данным id не существует");
        }

        return friendsStorage.getListOfFriends(id);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        if (user == null) {
            throw new NotFoundException("Невозможно создать пользователя. Передано пустое значение пользователя.");
        }
        userNameValidation(user);

        return userStorage.createUser(user);
    }

    public void deleteUser(Long id) {
        if (userStorage.getUser(id).isEmpty()) {
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        userStorage.deleteUser(id);
    }

    public User updateUser(User user) {
        if (userStorage.getUser(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        userNameValidation(user);

        return userStorage.updateUser(user);
    }

    @Override
    public User getById(Long id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует"));
    }

    public List<Event> getFeed(Long id) {
        return eventStorage.getFeed(id);
    }

    public List<Film> getRecommendationsFilm(Long id) {
        if (userStorage.getUser(id).isEmpty()) {
            throw new NotFoundException("Пользователя с данным id не существует");
        }

        return filmStorage.getRecommendationsFilm(id);
    }

    private void userNameValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}

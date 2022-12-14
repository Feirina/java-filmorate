package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.CRUD;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventDaoStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDaoStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.model.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.model.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.Operation.REMOVE;

@Service
public class UserService implements FilmorateService<User>, CRUD<User> {
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
        if (userStorage.getById(id).isEmpty() || userStorage.getById(friendId).isEmpty()) {
            throw new NotFoundException("Невозможно добавить в друзья - пользователя с данным id не существует");
        }
        friendsStorage.addToFriends(id, friendId);
        eventStorage.fixEvent(id, friendId, FRIEND, ADD);
    }

    public void removeFromFriends(Long id, Long friendId) {
        if (userStorage.getById(id).isEmpty() || userStorage.getById(friendId).isEmpty()) {
            throw new NotFoundException("Невозможно удалить из друзей - пользователя с данным id не существует");
        }
        friendsStorage.removeFromFriends(id, friendId);
        eventStorage.fixEvent(id, friendId, FRIEND, REMOVE);
    }

    public List<User> getListOfMutualFriends(Long id, Long otherId) {
        if (userStorage.getById(id).isEmpty() || userStorage.getById(otherId).isEmpty()) {
            throw new NotFoundException("Невозможно получить список общих друзей - пользователя с данным id не существует");
        }

        return friendsStorage.getListOfMutualFriends(id, otherId);
    }

    public List<User> getListOfFriends(Long id) {
        if (userStorage.getById(id).isEmpty()) {
            throw new NotFoundException("Невозможно получить список друзей - пользователя с данным id не существует");
        }

        return friendsStorage.getListOfFriends(id);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        if (user == null) {
            throw new NotFoundException("Невозможно создать пользователя. Передано пустое значение пользователя.");
        }
        userNameValidation(user);

        return userStorage.create(user);
    }

    public void delete(Long id) {
        if (userStorage.getById(id).isEmpty()) {
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        userStorage.delete(id);
    }

    public User update(User user) {
        if (userStorage.getById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        userNameValidation(user);

        return userStorage.update(user);
    }

    @Override
    public User getById(Long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует"));
    }

    public List<Event> getFeed(Long id) {
        return eventStorage.getFeed(id);
    }

    public List<Film> getRecommendationsFilm(Long id) {
        if (userStorage.getById(id).isEmpty()) {
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

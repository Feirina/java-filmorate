package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserStorage {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addToFriends(Long id, Long friendId) {
        if (!inMemoryUserStorage.getUsers().containsKey(friendId)) {
            throw new NotFoundException("Невозможно добавить в друзья - пользователя с данным friendId не существует");
        }
        getUser(id).getFriends().add(friendId);
        getUser(friendId).getFriends().add(id);
    }

    public void removeFromFriends(Long id, Long friendId) {
        getUser(id).getFriends().remove(friendId);
    }

    public List<User> getListOfMutualFriends(Long id, Long otherId) {
        List<User> listOfMutualFriends = new ArrayList<>();
        for (Long friendId : getUser(id).getFriends()) {
            if (getUser(otherId).getFriends().contains(friendId)) {
                listOfMutualFriends.add(inMemoryUserStorage.getUsers().get(friendId));
            }
        }
        return listOfMutualFriends;
    }

    public List<User> getListOfFriends(Long id) {
        List<User> listOfFriends = new ArrayList<>();
        for (Long friendId : getUser(id).getFriends()) {
            listOfFriends.add(inMemoryUserStorage.getUsers().get(friendId));
        }
        return listOfFriends;
    }

    @Override
    public List<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    @Override
    public User createUser(User user) {
        return inMemoryUserStorage.createUser(user);
    }

    @Override
    public void deleteUser(Long id) {
        inMemoryUserStorage.deleteUser(id);
    }

    @Override
    public User updateUser(User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    @Override
    public User getUser(Long id) {
        return inMemoryUserStorage.getUser(id);
    }
}

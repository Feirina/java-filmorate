package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsDaoStorage {
    List<User> getListOfFriends(Long userId);
    void addToFriends(Long userId, Long friendId);
    void removeFromFriends (Long userId, Long friendId);
    List<User> getListOfMutualFriends(Long userId, Long otherId);
}

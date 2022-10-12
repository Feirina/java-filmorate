package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.common.CRUD;
import ru.yandex.practicum.filmorate.common.Filmorate;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage extends Filmorate<User>, CRUD<User> {

}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        if (!userService.getInMemoryUserStorage().getMap().containsKey(id)) {
            log.error("При попытке получить данные пользователя возникла ошибка");
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        return userService.getUser(id);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        if (!userService.getInMemoryUserStorage().getMap().containsKey(id)) {
            log.error("При попытке удалить пользователя возникла ошибка");
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        userService.deleteUser(id);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (!userService.getInMemoryUserStorage().getMap().containsKey(user.getId())) {
            log.error("При попытке обновить данные пользователя возникла ошибка");
            throw new NotFoundException("Пользователя с данным id не существует");
        }
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addUserToFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteUserFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getListOfFriends(@PathVariable Long id) {
        return userService.getListOfFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getListOfMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getListOfMutualFriends(id, otherId);
    }
}

package ru.yandex.practicum.filmorate.dbtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTests {
    @Autowired
    private UserController userController;
    final private User user = User.builder()
            .email("email@co")
            .login("login")
            .name("name")
            .birthday(LocalDate.of(2000, 4, 15))
            .build();
    final private User user2 = User.builder()
            .email("email@co")
            .login("login")
            .name("name")
            .birthday(LocalDate.of(2000, 4, 15))
            .build();

    @Test
    void createTest() {
        final User user1 = userController.create(user);
        assertEquals(user1, userController.getUser(user1.getId()));
    }

    @Test
    void updateTest() {
        final User user1 = userController.create(user);
        final User user2 = user1;
        user2.setName("name2");
        userController.updateUser(user2);
        assertEquals(user2, userController.getUser(user2.getId()));
    }

    @Test
    void deleteTest() {
        final User user1 = userController.create(user);
        assertEquals(user1, userController.getUser(user1.getId()));
        List<User> users = userController.getAll();
        userController.deleteUser(user1.getId());
        assertEquals(users.size() - 1, userController.getAll().size());
    }

    @Test
    void addUserToFriendsTest() {
        final User user3 = userController.create(user);
        final User user4 = userController.create(user2);
        userController.addUserToFriends(user3.getId(), user4.getId());
        userController.addUserToFriends(user4.getId(), user3.getId());
        assertEquals(List.of(user4), userController.getListOfFriends(user3.getId()));
        assertEquals(List.of(user3), userController.getListOfFriends(user4.getId()));
    }

    @Test
    void deleteUserFromFriendsTest() {
        final User user3 = userController.create(user);
        final User user4 = userController.create(user2);
        userController.addUserToFriends(user3.getId(), user4.getId());
        userController.deleteUserFromFriends(user3.getId(), user4.getId());
        assertEquals(Collections.EMPTY_LIST, userController.getListOfFriends(user3.getId()));
    }

    @Test
    void getListOfMutualFriendsTest() {
        final User user3 = userController.create(user);
        final User user4 = userController.create(user2);
        final User user5 = userController.create(user2.toBuilder().email("mail@mail").login("login5").build());
        userController.addUserToFriends(user3.getId(), user4.getId());
        userController.addUserToFriends(user3.getId(), user5.getId());
        userController.addUserToFriends(user4.getId(), user3.getId());
        userController.addUserToFriends(user4.getId(), user5.getId());
        userController.addUserToFriends(user5.getId(), user3.getId());
        userController.addUserToFriends(user5.getId(), user4.getId());
        assertEquals(List.of(user3), userController.getListOfMutualFriends(user4.getId(), user5.getId()));
    }

    @Test
    void friendAddGetFeedTest() {
        final User user3 = userController.create(user);
        final User user4 = userController.create(user2);
        final Event testEvent = Event.builder()
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .entityId(2L)
                .userId(1L)
                .build();
        userController.addUserToFriends(user3.getId(), user4.getId());
        assertEquals(List.of(testEvent), userController.getFeed(1L));
    }

    @Test
    void friendRemoveGetFeedTest() {
        final User user3 = userController.create(user);
        final User user4 = userController.create(user2);
        final Event addEvent = Event.builder()
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .entityId(2L)
                .userId(1L)
                .build();
        final Event removeEvent = Event.builder()
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .entityId(2L)
                .userId(1L)
                .build();
        userController.addUserToFriends(user3.getId(), user4.getId());
        userController.deleteUserFromFriends(user3.getId(), user4.getId());
        assertEquals(List.of(addEvent, removeEvent), userController.getFeed(1L));
    }
}

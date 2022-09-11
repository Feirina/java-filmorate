package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class FilmorateApplicationTests {
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    @Test
    void nameOfFilmEmptyTest() {
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> filmController.create(Film.builder()
                        .name("")
                        .description("descr")
                        .releaseDate(LocalDate.of(2000, 1, 28))
                        .duration(120)
                        .build()));
        Assertions.assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void descriptionOfFilmTooLongTest() {
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> filmController.create(Film.builder()
                        .name("Interstellar")
                        .description("Interstellar is a 2014 epic science fiction film co-written, directed and produced" +
                                "by Christopher Nolan. It stars Matthew McConaughey, Anne Hathaway, Jessica Chastain, " +
                                "Bill Irwin, Ellen Burstyn, Matt Damon, and Michael Caine. Set in a dystopian future " +
                                "where humanity is struggling to survive, the film follows a group of astronauts who " +
                                "travel through a wormhole near Saturn in search of a new home for mankind.")
                        .releaseDate(LocalDate.of(2014, 10, 26))
                        .duration(150)
                        .build()));
        Assertions.assertEquals("Длина описания фильма не может превышать 200 символов", exception.getMessage());
    }

    @Test
    void invalidReleaseDateTest() {
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> filmController.create(Film.builder()
                        .name("Film")
                        .description("descr")
                        .releaseDate(LocalDate.of(1895, 12, 27))
                        .duration(150)
                        .build()));
        Assertions.assertEquals("Дата релиза фильма не может быть раньше 28.12.1895", exception.getMessage());
    }

    @Test
    void durationOfFilmTest() {
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> filmController.create(Film.builder()
                        .name("Film")
                        .description("descr")
                        .releaseDate(LocalDate.of(1995, 12, 27))
                        .duration(-10)
                        .build()));
        Assertions.assertEquals("Продолжительность фильма не может быть отрицательным значением",
                exception.getMessage());
    }

    @Test
    void invalidEmailTest() {
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> userController.create(User.builder()
                        .email("")
                        .login("login")
                        .name("name")
                        .birthday(LocalDate.of(1998, 4, 29))
                        .build()));
        Assertions.assertEquals("Введен неккоректный e-mail адрес", exception.getMessage());
    }

    @Test
    void invalidLoginTest() {
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> userController.create(User.builder()
                        .email("email@com")
                        .login("")
                        .name("name")
                        .birthday(LocalDate.of(1998, 4, 29))
                        .build()));
        Assertions.assertEquals("Введен неккоректный логин", exception.getMessage());
    }

    @Test
    void emptyNameOfUserTest() {
        User user = userController.create(User.builder()
                .email("email@com")
                .login("login")
                .name("")
                .birthday(LocalDate.of(1998, 4, 29))
                .build());
        Assertions.assertEquals("login", user.getName());
    }

    @Test
    void invalidBirthdayDateTest() {
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> userController.create(User.builder()
                        .email("email@com")
                        .login("login")
                        .name("name")
                        .birthday(LocalDate.of(2023, 4, 29))
                        .build()));
        Assertions.assertEquals("Введена неккоректная дата рождения", exception.getMessage());
    }

    @Test
    void addLikeToFilmTest() {
        Film film = filmController.create(Film.builder()
                .name("Film")
                .description("descr")
                .releaseDate(LocalDate.of(2006, 11, 12))
                .duration(120).build());
        Assertions.assertEquals(0, film.getCountOfLike());
        User user = userController.create(User.builder().email("ya@ya.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1995, 10, 4))
                .build());
        filmController.addLikeToFilm(1L, 1L);
        Assertions.assertEquals(1, film.getCountOfLike());
        filmController.addLikeToFilm(1L, 1L);
        Assertions.assertEquals(1, film.getCountOfLike());
    }

    @Test
    void deleteLikeOfFilmTest() {
        Film film = filmController.create(Film.builder()
                .name("Film")
                .description("descr")
                .releaseDate(LocalDate.of(2006, 11, 12))
                .duration(120)
                .build());
        User user = userController.create(User.builder()
                .email("ya@ya.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1995, 10, 4))
                .build());
        filmController.addLikeToFilm(1L, 1L);
        Assertions.assertEquals(1, film.getCountOfLike());
        filmController.deleteLikeOfFilm(1L, 1L);
        Assertions.assertEquals(0, film.getCountOfLike());
    }

    @Test
    void getMostPopularFilmsTest() {
        Assertions.assertEquals(0, filmController.getMostPopularFilms(5).size());
        Film film1 = filmController.create(Film.builder()
                .name("Film1")
                .description("descr1")
                .releaseDate(LocalDate.of(2006, 11, 12))
                .duration(120)
                .countOfLike(4)
                .build());
        Film film2 = filmController.create(Film.builder()
                .name("Film2")
                .description("descr2")
                .releaseDate(LocalDate.of(2005, 12, 12))
                .duration(120)
                .countOfLike(7)
                .build());
        Film film3 = filmController.create(Film.builder()
                .name("Film3")
                .description("descr3")
                .releaseDate(LocalDate.of(2000, 12, 25))
                .duration(120)
                .countOfLike(5)
                .build());
        Assertions.assertEquals(3, filmController.getMostPopularFilms(5).size());
        Assertions.assertEquals(film3, filmController.getMostPopularFilms(5).get(1));
    }

    @Test
    void addToFriendsTest() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userController.addUserToFriends(1L, 1L));
        Assertions.assertEquals("Невозможно добавить в друзья - пользователя с данным friendId не существует",
                exception.getMessage());
        User user = userController.create(User.builder()
                .email("ya@ya.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1995, 10, 4))
                .build());
        Assertions.assertEquals(0, user.getFriends().size());
        User user2 = userController.create(User.builder()
                .email("ya2@ya.ru")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(1994, 9, 24))
                .build());
        Assertions.assertEquals(0, user2.getFriends().size());
        userController.addUserToFriends(1L, 2L);
        Assertions.assertEquals(1, user.getFriends().size());
        Assertions.assertEquals(1, user2.getFriends().size());
    }

    @Test
    void removeFromFriendsTest() {
        User user = userController.create(User.builder()
                .email("ya@ya.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1995, 10, 4))
                .build());
        User user2 = userController.create(User.builder()
                .email("ya2@ya.ru")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(1994, 9, 24))
                .build());
        userController.addUserToFriends(1L, 2L);
        Assertions.assertEquals(1, user.getFriends().size());
        Assertions.assertEquals(1, user2.getFriends().size());
        userController.deleteUserFromFriends(1L, 2L);
        Assertions.assertEquals(0, user.getFriends().size());
        Assertions.assertEquals(0, user2.getFriends().size());
    }

    @Test
    void getListOfFriendsAndMutualFriendsTest() {
        User user = userController.create(User.builder()
                .email("ya@ya.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1995, 10, 4))
                .build());
        User user2 = userController.create(User.builder()
                .email("ya2@ya.ru")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(1994, 9, 24))
                .build());
        User user3 = userController.create(User.builder()
                .email("ya3@ya.ru")
                .login("login3")
                .name("name3")
                .birthday(LocalDate.of(1992, 4, 2))
                .build());
        userController.addUserToFriends(1L, 2L);
        userController.addUserToFriends(2L, 3L);
        Assertions.assertEquals(user2, userController.getListOfFriends(1L).get(0));
        Assertions.assertEquals(user2, userController.getListOfMutualFriends(1L, 3L).get(0));
    }
}

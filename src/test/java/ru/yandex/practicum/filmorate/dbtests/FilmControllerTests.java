package ru.yandex.practicum.filmorate.dbtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmControllerTests {
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    @Autowired
    private DirectorController directorController;
    final private Film film = Film.builder()
            .name("name")
            .description("new Film")
            .duration(130)
            .releaseDate(LocalDate.of(2002, 5, 20))
            .mpa(Mpa.builder().id(1L).name("G").build())
            .build();
    final private User user = User.builder()
            .email("email@co")
            .login("login")
            .name("name")
            .birthday(LocalDate.of(2000, 4, 15))
            .build();

    @Test
    void createTest() {
        final Film film1 = filmController.create(film);
        assertEquals(film1, filmController.getFilm(film1.getId()));
    }

    @Test
    void updateTest() {
        final Film film1 = filmController.create(film);
        final Film film2 = film1;
        film2.setDuration(100);
        filmController.updateFilm(film2);
        assertEquals(film2, filmController.getFilm(film2.getId()));
    }

    @Test
    void deleteTest() {
        final Film film1 = filmController.create(film);
        assertEquals(film1, filmController.getFilm(film1.getId()));
        List<Film> films = filmController.getAll();
        filmController.deleteFilm(film1.getId());
        assertEquals(films.size() - 1, filmController.getAll().size());
    }

    @Test
    void addLikeToFilmTest() {
        final Film film1 = filmController.create(film);
        final Film film2 = filmController.create(film);
        final User user1 = userController.create(user);
        filmController.addLikeToFilm(film2.getId(), user1.getId());
        assertEquals(film2, filmController.getMostPopularFilms(1).stream()
                .findFirst().orElse(null));
    }

    @Test
    void deleteLikeOfFilmTest() {
        final Film film1 = filmController.create(film);
        final Film film2 = filmController.create(film);
        final User user1 = userController.create(user);
        filmController.addLikeToFilm(film1.getId(), user1.getId());
        filmController.deleteLikeOfFilm(film1.getId(), user1.getId());
        filmController.addLikeToFilm(film2.getId(), user1.getId());
        assertEquals(film2, filmController.getMostPopularFilms(1).stream()
                .findFirst().orElse(null));
    }

    @Test
    void searchFilmTest() {
        createFilmsAndUserForTest();
        List<Film> findFilms = filmController.searchFilm("i", List.of("title"));
        assertEquals(2, findFilms.size());
        assertEquals("film", findFilms.get(0).getName());
        findFilms = filmController.searchFilm("name", List.of("title"));
        assertEquals(1, findFilms.size());
        assertEquals(film.getName(), findFilms.get(0).getName());
        assertNull(filmController.searchFilm("i", List.of("zero")));
        findFilms = filmController.searchFilm("Test", List.of("director"));
        assertEquals(1, findFilms.size());
        assertEquals("title", findFilms.get(0).getName());
        findFilms = filmController.searchFilm("T", List.of("director"));
        assertEquals(2, findFilms.size());
        assertEquals("film", findFilms.get(0).getName());
        findFilms = filmController.searchFilm("name", List.of("title", "director"));
        assertEquals(1, findFilms.size());
        assertEquals(film.getName(), findFilms.get(0).getName());
        findFilms = filmController.searchFilm("test", List.of("title", "director"));
        assertEquals(1, findFilms.size());
    }

    private void createFilmsAndUserForTest() {
        filmController.create(film);
        Set<Director> directors = new HashSet<>();
        Set<Director> directors1 = new HashSet<>();
        createDirectorForTest();
        final Film film1 = Film.builder()
                .name("title")
                .description("new Film")
                .duration(130)
                .releaseDate(LocalDate.of(2002, 5, 20))
                .mpa(Mpa.builder().id(1L).name("G").build())
                .build();
        directors.add(directorController.getDirector(1L));
        film1.setDirectors(directors);
        filmController.create(film1);
        final Film film2 = Film.builder()
                .name("film")
                .description("new Film")
                .duration(130)
                .releaseDate(LocalDate.of(2002, 5, 20))
                .mpa(Mpa.builder().id(1L).name("G").build())
                .build();
        directors1.add(directorController.getDirector(2L));
        film2.setDirectors(directors1);
        filmController.create(film2);
        userController.create(user);
        filmController.addLikeToFilm(film2.getId(), user.getId());
    }

    public void createDirectorForTest() {
        final Director director = Director.builder()
                .name("Test")
                .build();
        directorController.create(director);
        final Director director1 = Director.builder()
                .name("T")
                .build();
        directorController.create(director1);
    }
}

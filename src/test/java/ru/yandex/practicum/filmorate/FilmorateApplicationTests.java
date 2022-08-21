package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {
	private FilmController filmController;
	private UserController userController;

	@BeforeEach
	void init() {
		filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
		userController = new UserController(new UserService(new InMemoryUserStorage()));
	}

	@Test
	void nameOfFilmEmptyTest() {
		final ValidationException exception = Assertions.assertThrows(ValidationException.class,
				() -> filmController.create(Film.builder()
						.name("")
						.description("descr")
						.releaseDate(LocalDate.of(2000, 1,28))
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
						.birthday(LocalDate.of(1998, 4,29))
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
						.birthday(LocalDate.of(1998, 4,29))
						.build()));
		Assertions.assertEquals("Введен неккоректный логин", exception.getMessage());
	}

	@Test
	void emptyNameOfUserTest() {
		User user = userController.create(User.builder()
						.email("email@com")
						.login("login")
						.name("")
						.birthday(LocalDate.of(1998, 4,29))
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
						.birthday(LocalDate.of(2023, 4,29))
						.build()));
		Assertions.assertEquals("Введена неккоректная дата рождения", exception.getMessage());
	}
}

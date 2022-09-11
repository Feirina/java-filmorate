package ru.yandex.practicum.filmorate.dbtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.GenreController;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GenreControllerTests {
    @Autowired
    private GenreController genreController;

    @Test
    void getAllGenresTest() {
        assertEquals(6, genreController.getAllGenre().size());
    }

    @Test
    void getGenreById() {
        String genre = " омеди€";
        assertEquals(genre, genreController.getGenreById(1).getName()); //сравнивать со строкой прописанной в equals
                                                                        // отказываетс€, кодировкку мен€ть пробовала
    }
}

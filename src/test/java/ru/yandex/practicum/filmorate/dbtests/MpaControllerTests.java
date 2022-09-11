package ru.yandex.practicum.filmorate.dbtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.MpaController;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MpaControllerTests {
    @Autowired
    private MpaController mpaController;

    @Test
    void getAllRatingTest() {
        assertEquals(5, mpaController.getAllRating().size());
    }

    @Test
    void getGenreByIdTest() {
        assertEquals("G", mpaController.getRatingById(1).getName());
    }
}

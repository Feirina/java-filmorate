package ru.yandex.practicum.filmorate.dbtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewControllerTests {
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    @Autowired
    private ReviewController reviewController;
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

    final private Review review = Review.builder()
            .content("content")
            .isPositive(true)
            .userId(1L)
            .filmId(1L)
            .useful(0L)
            .build();

    @Test
    void createTest() {
        final Film film1 = filmController.create(film);
        final User user1 = userController.create(user);
        final Review review1 = reviewController.create(review);
        assertEquals(review1, reviewController.getReview(review1.getReviewId()));
    }

    @Test
    void updateTest() {
        final Film film1 = filmController.create(film);
        final User user1 = userController.create(user);
        final Review review1 = reviewController.create(review);
        final Review review2 = review1;
        review2.setContent("content1");
        reviewController.updateReview(review2);
        assertEquals(review2, reviewController.getReview(review2.getReviewId()));
    }

    @Test
    void deleteTest() {
        final Film film1 = filmController.create(film);
        final User user1 = userController.create(user);
        final Review review1 = reviewController.create(review);
        assertEquals(review1, reviewController.getReview(review1.getReviewId()));
        List<Review> reviews = reviewController.getAllReview(null, null);
        reviewController.deleteReview(review1.getReviewId());
        assertEquals(reviews.size() - 1, filmController.getAll().size());
    }

    @Test
    void addLikeToReviewTest() {
        final Film film1 = filmController.create(film);
        final User user1 = userController.create(user);
        final Review review1 = reviewController.create(review);
        reviewController.addLikeToReview(film1.getId(), user1.getId());
        assertEquals(1, reviewController.getReview(review1.getReviewId()).getUseful());
    }

    @Test
    void deleteLikeOfReviewTest() {
        final Film film1 = filmController.create(film);
        final User user1 = userController.create(user);
        final Review review1 = reviewController.create(review);
        reviewController.addLikeToReview(film1.getId(), user1.getId());
        assertEquals(1, reviewController.getReview(review1.getReviewId()).getUseful());

        reviewController.deleteLikeOfReview(film1.getId(), user1.getId());
        assertEquals(0, reviewController.getReview(review1.getReviewId()).getUseful());
    }

    @Test
    void addDislikeToReviewTest() {
        final Film film1 = filmController.create(film);
        final User user1 = userController.create(user);
        final Review review1 = reviewController.create(review);
        reviewController.addDislikeToReview(film1.getId(), user1.getId());
        assertEquals(-1, reviewController.getReview(review1.getReviewId()).getUseful());
    }

    @Test
    void deleteDislikeOfReviewTest() {
        final Film film1 = filmController.create(film);
        final User user1 = userController.create(user);
        final Review review1 = reviewController.create(review);
        reviewController.addDislikeToReview(film1.getId(), user1.getId());
        assertEquals(-1, reviewController.getReview(review1.getReviewId()).getUseful());

        reviewController.deleteDislikeOfReview(film1.getId(), user1.getId());
        assertEquals(0, reviewController.getReview(review1.getReviewId()).getUseful());
    }
}

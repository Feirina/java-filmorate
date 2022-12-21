package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.CRUD;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDaoStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.Operation.*;


@Service
public class ReviewService implements FilmorateService<Review>, CRUD<Review> {
    private final ReviewDaoStorage reviewStorage;

    private final UserStorage userStorage;

    private final FilmStorage filmStorage;

    private final EventDbStorage eventStorage;

    @Autowired
    public ReviewService(ReviewDaoStorage reviewStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage,
                         @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                         EventDbStorage eventStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
    }

    public Review create(Review review) {
        if (review == null) {
            throw new NotFoundException("Невозможно создать отзыв. Передано пустое значение отзыва.");
        }
        if (review.getUserId() <= 0 || userStorage.getById(review.getUserId()).isEmpty()) {
            throw new NotFoundException("Невозможно добавить отзыв, " +
                    "пользователя с данным id не существует");
        }
        if (review.getFilmId() <= 0 || filmStorage.getById(review.getFilmId()).isEmpty()) {
            throw new NotFoundException("Невозможно добавить отзыв, фильма с данным id не существует");
        }
        Review createdReview = reviewStorage.create(review);
        eventStorage.fixEvent(createdReview.getUserId(), createdReview.getReviewId(), REVIEW, ADD);

        return createdReview;
    }

    public Review update(Review review) {
        Optional<Review> reviewResponse = reviewStorage.getById(review.getReviewId());
        if (reviewResponse.isEmpty()) {
            throw new NotFoundException("Отзыва с данным id не существует");
        }
        Review foundReview = reviewResponse.get();
        foundReview.setContent(review.getContent());
        foundReview.setIsPositive(review.getIsPositive());
        eventStorage.fixEvent(foundReview.getUserId(), foundReview.getReviewId(), REVIEW, UPDATE);

        return reviewStorage.update(foundReview);
    }

    public void delete(Long id) {
        Optional<Review> review = reviewStorage.getById(id);
        if (review.isEmpty()) {
            throw new NotFoundException("Отзыва с данным id не существует");
        }
        eventStorage.fixEvent(review.get().getUserId(), id, REVIEW, REMOVE);
        reviewStorage.delete(id);
    }

    @Override
    public List<Review> getAll() {
        return reviewStorage.getAll();
    }

    @Override
    public Review getById(Long id) {
        return reviewStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Отзыва с данным id не существует"));
    }

    public List<Review> getAllReviewsByFilmId(Long filmId, Integer count) {
        if (filmId == null) {

            return reviewStorage.getAllReviewsByFilmId(filmId, 10);
        }
        return reviewStorage.getAllReviewsByFilmId(filmId, Objects.requireNonNullElse(count, 10));

    }

    public void addLikeToReview(Long id, Long userId) {
        if (reviewStorage.getById(id).isEmpty()) {
            throw new NotFoundException("Невозможно добавить лайк отзыва с данным id не существует");
        } else if (userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("Невозможно добавить лайк пользователя с данным id не существует");
        }
        reviewStorage.addLikeToReview(id, userId);
    }

    public void addDislikeToReview(Long id, Long userId) {
        if (reviewStorage.getById(id).isEmpty()) {
            throw new NotFoundException("Невозможно добавить дизлайк отзыва с данным id не существует");
        } else if (userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("Невозможно добавить дизлайк пользователя с данным id не существует");
        }
        reviewStorage.addDislikeToReview(id, userId);
    }

    public void deleteLikeOfReview(Long id, Long userId) {
        if (reviewStorage.getById(id).isEmpty()) {
            throw new NotFoundException("Невозможно удалить лайк отзыва с данным id не существует");
        } else if (userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("Невозможно удалить лайк пользователя с данным id не существует");
        }
        reviewStorage.deleteLikeOfReview(id, userId);
    }

    public void deleteDislikeOfReview(Long id, Long userId) {
        if (reviewStorage.getById(id).isEmpty()) {
            throw new NotFoundException("Невозможно удалить дизлайк отзыва с данным id не существует");
        } else if (userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("Невозможно удалить дизлайк пользователя с данным id не существует");
        }
        reviewStorage.deleteDislikeOfReview(id, userId);
    }
}

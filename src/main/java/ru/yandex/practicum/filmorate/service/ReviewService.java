package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDaoStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService implements FilmorateService<Review> {
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

    public Review addReview(Review review) {
        if (review.getUserId() <= 0 || userStorage.getUser(review.getUserId()) == null) {
            throw new NotFoundException("Невозможно добавить отзыв, " +
                    "пользователя с данным id не существует");
        }
        if (review.getFilmId() <= 0 || filmStorage.getFilm(review.getFilmId()) == null) {
            throw new NotFoundException("Невозможно добавить отзыв, фильма с данным id не существует");
        }
        Review createdReview = reviewStorage.addReview(review);
        eventStorage.fixEvent(createdReview.getUserId(), createdReview.getReviewId(), EventType.REVIEW, Operation.ADD);
        return createdReview;
    }

    public Review updateReview(Review review) {
        Review foundReview = reviewStorage.getReview(review.getReviewId());
        if (foundReview == null) {
            throw new NotFoundException("Отзыва с данным id не существует");
        }
        Review updatedReview = reviewStorage.updateReview(review);
        eventStorage.fixEvent(updatedReview.getUserId(), updatedReview.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        return updatedReview;
    }

    public void deleteReview(Long id) {
        Review review = reviewStorage.getReview(id);
        if (review == null) {
            throw new NotFoundException("Отзыва с данным id не существует");
        }
        eventStorage.fixEvent(review.getUserId(), id, EventType.REVIEW, Operation.REMOVE);
        reviewStorage.deleteReview(id);
    }

    @Override
    public List<Review> getAll() {
        return reviewStorage.getAll();
    }

    @Override
    public Review getById(Long id) {
        Review review = reviewStorage.getReview(id);
        if (review == null) {
            throw new NotFoundException("Отзыва с данным id не существует");
        }
        return review;
    }

    public List<Review> getAllReviewsByFilmId(Long filmId, Integer count) {
        if (filmId == null) {
            return reviewStorage.getAllReviewsByFilmId(filmId, 10);
        }
        if (count == null) {
            return reviewStorage.getAllReviewsByFilmId(filmId, 10);
        }
        return reviewStorage.getAllReviewsByFilmId(filmId, count);
    }

    public void addLikeToReview(Long id, Long userId) {
        if (reviewStorage.getReview(id) == null) {
            throw new NotFoundException("Невозможно добавить лайк отзыва с данным id не существует");
        } else if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Невозможно добавить лайк пользователя с данным id не существует");
        }
        reviewStorage.addLikeToReview(id, userId);
    }

    public void addDislikeToReview(Long id, Long userId) {
        if (reviewStorage.getReview(id) == null) {
            throw new NotFoundException("Невозможно добавить дизлайк отзыва с данным id не существует");
        } else if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Невозможно добавить дизлайк пользователя с данным id не существует");
        }
        reviewStorage.addDislikeToReview(id, userId);
    }

    public void deleteLikeOfReview(Long id, Long userId) {
        if (reviewStorage.getReview(id) == null) {
            throw new NotFoundException("Невозможно удалить лайк отзыва с данным id не существует");
        } else if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Невозможно удалить лайк пользователя с данным id не существует");
        }
        reviewStorage.deleteLikeOfReview(id, userId);
    }

    public void deleteDislikeOfReview(Long id, Long userId) {
        if (reviewStorage.getReview(id) == null) {
            throw new NotFoundException("Невозможно удалить дизлайк отзыва с данным id не существует");
        } else if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Невозможно удалить дизлайк пользователя с данным id не существует");
        }
        reviewStorage.deleteDislikeOfReview(id, userId);
    }
}

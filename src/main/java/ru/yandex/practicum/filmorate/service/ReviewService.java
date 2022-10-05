package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDaoStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
public class ReviewService implements FilmorateService<Review> {
    private final ReviewDaoStorage reviewStorage;
    private final UserStorage userStorage;

    private final FilmStorage filmStorage;

    @Autowired
    public ReviewService(ReviewDaoStorage reviewStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage,
                         @Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review addReview(Review review) {
        validation(review);
        if (review.getUserId() <= 0 || userStorage.getUser(review.getUserId()) == null) {
            throw new NotFoundException("Невозможно добавить отзыв, " +
                    "пользователя с данным id не существует");
        }
        if (review.getFilmId() <= 0 || filmStorage.getFilm(review.getFilmId()) == null) {
            throw new NotFoundException("Невозможно добавить отзыв, фильма с данным id не существует");
        }
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        validation(review);
        if (reviewStorage.getReview(review.getReviewId()) == null) {
            throw new NotFoundException("Отзыва с данным id не существует");
        }
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long id) {
        if (reviewStorage.getReview(id) == null) {
            throw new NotFoundException("Отзыва с данным id не существует");
        }
        reviewStorage.deleteReview(id);
    }

    @Override
    public List<Review> getAll() {
        List<Review> reviews = reviewStorage.getAll();
//        for (Review review : reviews) {
//            review.setUseful(reviewStorage.setUsefulForReview(review.getReviewId()));
//        }
        return reviews;
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
            List<Review> reviews = reviewStorage.getAllReviewsByFilmId(filmId, 10);
            return reviews;
        }
        if (count == null) {
            List<Review> reviews = reviewStorage.getAllReviewsByFilmId(filmId, 10);
            return reviews;
        }
        List<Review> reviews = reviewStorage.getAllReviewsByFilmId(filmId, count);
        return reviews;
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

    public void validation(@Valid @RequestBody Review review) {
        if (review.getUserId() == null) {
            log.error("При попытке создать или обновить отзыв произошла ошибка id пользователя");
            throw new ValidationException("Id пользователя не может быть пустым");
        }
        if (review.getFilmId() == null) {
            log.error("При попытке создать или обновить отзыв произошла ошибка id фильма");
            throw new ValidationException("Id фильма не может быть пустым");
        }
        if (review.getContent() == null || review.getContent().isBlank()) {
            log.error("При попытке создать или обновить отзыв произошла ошибка содержания отзыва");
            throw new ValidationException("Содержание отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            log.error("При попытке создать или обновить отзыв произошла ошибка ценки отзыва");
            throw new ValidationException("Содержание оценки отзыва не может быть пустым");
        }
    }
}

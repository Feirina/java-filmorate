package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.common.CRUD;
import ru.yandex.practicum.filmorate.common.Filmorate;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDaoStorage extends Filmorate<Review>, CRUD<Review> {
    List<Review> getAllReviewsByFilmId(Long filmId, int count);

    void addLikeToReview(Long id, Long userId);

    void addDislikeToReview(Long id, Long userId);

    void deleteLikeOfReview(Long id, Long userId);

    void deleteDislikeOfReview(Long id, Long userId);
}

package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDaoStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long id);

    Optional<Review> getReview(Long id);

    List<Review> getAllReviewsByFilmId(Long filmId, int count);

    List<Review> getAll();

    void addLikeToReview(Long id, Long userId);

    void addDislikeToReview(Long id, Long userId);

    void deleteLikeOfReview(Long id, Long userId);

    void deleteDislikeOfReview(Long id, Long userId);
}

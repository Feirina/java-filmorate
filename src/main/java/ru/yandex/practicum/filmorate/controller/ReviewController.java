package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @GetMapping
    public List<Review> getAllReview(@RequestParam(required = false) Long filmId,
                                     @RequestParam(required = false) Integer count) {
        if (filmId == null && count == null) {
            return reviewService.getAll();
        }
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeOfReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.deleteLikeOfReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeOfReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.deleteDislikeOfReview(id, userId);
    }
}

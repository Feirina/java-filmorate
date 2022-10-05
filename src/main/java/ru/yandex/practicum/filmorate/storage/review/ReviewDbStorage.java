package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.Mappers;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class ReviewDbStorage implements ReviewDaoStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Mappers mappers;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, Mappers mappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.mappers = mappers;
    }

    @Override
    public Review addReview(Review review) {
        if (review == null) {
            throw new NotFoundException("Невозможно создать отзыв. Передано пустое значение отзыва.");
        }
        makeReview(review);
        return review;
    }

    private void makeReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO reviews (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ID"});
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.getIsPositive());
            statement.setLong(3, review.getUserId());
            statement.setLong(4, review.getFilmId());
            return statement;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
    }

    @Override
    public Review updateReview(Review review) {
        final String sql = "UPDATE reviews SET CONTENT = ?, IS_POSITIVE = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(),
                review.getReviewId());
        return review;
    }

    @Override
    public void deleteReview(Long id) {
        final String sql = "DELETE FROM reviews WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Review getReview(Long id) {
        final String sql = "SELECT *, SUM(rl.like_value) FROM reviews AS r LEFT JOIN reviews_likes " +
                "AS rl ON r.id = rl.review_id WHERE r.id = ?";
        Review review = jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeReview(rs), id)
                .stream()
                .findAny().orElse(null);
        return review;
    }

    @Override
    public List<Review> getAllReviewsByFilmId(Long filmId, int count) {
        if (filmId == null) {
            final String sql = "SELECT *, SUM(rl.like_value) FROM reviews AS r LEFT JOIN reviews_likes " +
                    "AS rl ON r.id = rl.review_id GROUP BY r.id ORDER BY SUM(rl.like_value) DESC LIMIT ?";
            List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeReview(rs), count);
            return reviews;
        }
        final String sql = "SELECT *, SUM(rl.like_value) FROM reviews AS r LEFT JOIN reviews_likes " +
                "AS rl ON r.id = rl.review_id WHERE r.film_id = ? GROUP BY r.id ORDER BY SUM(rl.like_value) DESC LIMIT ?";
        List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeReview(rs), filmId, count);
        return reviews;
    }

    @Override
    public List<Review> getAll() {
        final String sql = "SELECT *, SUM(rl.like_value) FROM reviews AS r LEFT JOIN reviews_likes " +
                "AS rl ON r.id = rl.review_id " +
                "group by r.id ORDER BY case when SUM(rl.like_value)>0 then 0 else 1 end, SUM(rl.like_value)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeReview(rs));
    }

    @Override
    public void addLikeToReview(Long id, Long userId) {
        final String sql = "INSERT INTO reviews_likes (review_id, user_id, like_value) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, 1);
    }

    @Override
    public void addDislikeToReview(Long id, Long userId) {
        final String sql = "INSERT INTO reviews_likes (review_id, user_id, like_value) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, -1);
    }

    @Override
    public void deleteLikeOfReview(Long id, Long userId) {
        final String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? " +
                "AND LIKE_VALUE = ?";
        jdbcTemplate.update(sql, id, userId, 1);
    }

    @Override
    public void deleteDislikeOfReview(Long id, Long userId) {
        final String sql = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? " +
                "AND LIKE_VALUE = ?";
        jdbcTemplate.update(sql, id, userId, -1);
    }

    @Override
    public Long setUsefulForReview(Long id) {
        Long useful = Long.valueOf(0);
        SqlRowSet usefulRows = jdbcTemplate.queryForRowSet("select * from REVIEWS_LIKES where " +
                "REVIEW_ID = ?", id);
        while (usefulRows.next()) {
            useful = useful + usefulRows.getLong("like_value");
        }
        return useful;
    }


}

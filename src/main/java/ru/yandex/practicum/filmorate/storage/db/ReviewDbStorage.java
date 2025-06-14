package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.ReviewLikeRowMapper;
import ru.yandex.practicum.filmorate.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.Collection;
import java.util.Optional;

@Component
public class ReviewDbStorage extends BaseDbStorage {
    private final ReviewRowMapper reviewRowMapper;
    private final ReviewLikeRowMapper likeRowMapper;
    private static final String FIND_ALL = "SELECT * FROM reviews";
    private static final String CREATE =
            "INSERT INTO reviews (content, isPositive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE reviews SET content = ?, isPositive = ?, user_id = ?, film_id = ? WHERE review_id = ?";
    private static final String DELETE = "DELETE FROM reviews WHERE review_id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String FIND_ALL_BY_COUNT = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    private static final String FIND_ALL_BY_FILM_ID_AND_COUNT =
            "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
    private static final String LIKE_REVIEW = "INSERT INTO review_likes (review_id, user_id) VALUES (?, ?)";
    private static final String DISLIKE_REVIEW =
            "DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_USEFUL = "UPDATE reviews SET useful = ? WHERE review_id = ?";
    private static final String REMOVE_LIKE = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String REMOVE_DISLIKE = "DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?";
    private static final String FIND_LIKE_BY_REVIEW_ID_AND_USER_ID =
            "SELECT * FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String FIND_DISLIKE_BY_REVIEW_ID_AND_USER_ID =
            "SELECT * FROM review_dislikes WHERE review_id = ? AND user_id = ?";

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbc, ReviewRowMapper reviewRowMapper, ReviewLikeRowMapper likeRowMapper) {
        super(jdbc);
        this.reviewRowMapper = reviewRowMapper;
        this.likeRowMapper = likeRowMapper;
    }

    public Collection<Review> findAll() {
        return jdbc.query(FIND_ALL, reviewRowMapper);
    }

    public Review create(Review review) {
        Long id = insert(CREATE,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                0);
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    public Review update(Review review) {
        jdbc.update(UPDATE,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId());
        return review;
    }

    public void delete(long id) {
        jdbc.update(DELETE, id);
    }

    public Optional<Review> findById(long id) {
        return jdbc.query(FIND_BY_ID, reviewRowMapper, id).stream().findFirst();
    }

    public Collection<Review> findAllByCount(int count) {
        return jdbc.query(FIND_ALL_BY_COUNT, reviewRowMapper, count);
    }

    public Collection<Review> findAllByFilmIdAndCount(long filmId, int count) {
        return jdbc.query(FIND_ALL_BY_FILM_ID_AND_COUNT, reviewRowMapper, filmId, count);
    }

    public void likeReview(long reviewId, long userId, int useful) {
        jdbc.update(LIKE_REVIEW, reviewId, userId);
        jdbc.update(UPDATE_USEFUL, useful, reviewId);
    }

    public void dislikeReview(long reviewId, long userId, int useful) {
        jdbc.update(DISLIKE_REVIEW, reviewId, userId);
        jdbc.update(UPDATE_USEFUL, useful, reviewId);
    }

    public void removeLike(long reviewId, long userId, int useful) {
        jdbc.update(REMOVE_LIKE, reviewId, userId);
        jdbc.update(UPDATE_USEFUL, useful, reviewId);
    }

    public void removeDislike(long reviewId, long userId, int useful) {
        jdbc.update(REMOVE_DISLIKE, reviewId, userId);
        jdbc.update(UPDATE_USEFUL, useful);
    }

    public boolean likeExists(long reviewId, long userId) {
        Optional<ReviewLike> optionalReviewLike =
                jdbc.query(FIND_LIKE_BY_REVIEW_ID_AND_USER_ID, likeRowMapper, reviewId, userId)
                .stream()
                .findFirst();
        return optionalReviewLike.isPresent();
    }

    public boolean dislikeExists(long reviewId, long userId) {
        Optional<ReviewLike> optionalReviewLike =
                jdbc.query(FIND_DISLIKE_BY_REVIEW_ID_AND_USER_ID, likeRowMapper, reviewId, userId)
                        .stream()
                        .findFirst();
        return optionalReviewLike.isPresent();
    }
}
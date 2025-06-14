package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.InvalidUserInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private static final short LIKE_VALUE = 1;
    private static final short DISLIKE_VALUE = -1;
    private static final short DISLIKE_VALUE_IF_LIKE_EXISTS = -2;

    public Collection<Review> findAll() {
        return reviewStorage.findAll();
    }

    public Review create(Review review) {
        if (filmStorage.findFilmById(review.getFilmId()) == null) {
            log.warn("Фильм с id={} не найден", review.getFilmId());
            throw new NotFoundException("Фильм с id=" + review.getFilmId() + " не найден.");
        }
        checkUserExistence(review.getUserId());
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        Review foundReview = findReviewOrThrow(review.getReviewId());
        review.setReviewId(foundReview.getReviewId());
        review.setUseful(foundReview.getUseful());
        return reviewStorage.update(review);
    }

    public void delete(long id) {
        findReviewOrThrow(id);
        reviewStorage.delete(id);
    }

    public Review findById(long id) {
        return findReviewOrThrow(id);
    }

    public Collection<Review> findCountReviewsByFilmId(Long filmId, int count) {
        if (filmId == null) {
            return reviewStorage.findAllByCount(count);
        }
        if (filmStorage.findFilmById(filmId) == null) {
            log.warn("Фильм с id={} не найден.", filmId);
            throw new NotFoundException("Нет фильма с id=" + filmId);
        }
        return reviewStorage.findAllByFilmIdAndCount(filmId, count);
    }

    @Transactional
    public Review likeReview(long reviewId, long userId) {
        checkUserExistence(userId);
        Review review = findReviewOrThrow(reviewId);
        if (reviewStorage.likeExists(reviewId, userId)) {
            log.warn("Пользователь с id={} уже поставил лайк на отзыв с id={}", userId, reviewId);
            throw new InvalidUserInputException("Лайк уже стоит на отзыве с id=" + reviewId);
        }
        review.setUseful(review.getUseful() + LIKE_VALUE);
        reviewStorage.likeReview(reviewId, userId, review.getUseful());
        return review;
    }

    @Transactional
    public Review dislikeReview(long reviewId, long userId) {
        checkUserExistence(userId);
        Review review = findReviewOrThrow(reviewId);
        if (reviewStorage.dislikeExists(reviewId, userId)) {
            log.warn("Пользователь с id={} уже поставил дизлайк на отзыв с id={}", userId, reviewId);
            throw new InvalidUserInputException("Пользователь уже поставил дизлайк на отзыв с id=" + reviewId);
        }
        if (reviewStorage.likeExists(reviewId, userId)) {
            review.setUseful(review.getUseful() + DISLIKE_VALUE_IF_LIKE_EXISTS);
        } else {
            review.setUseful(review.getUseful() + DISLIKE_VALUE);
        }
        reviewStorage.dislikeReview(reviewId, userId, review.getUseful());
        return review;
    }

    @Transactional
    public Review removeLike(long reviewId, long userId) {
        checkUserExistence(userId);
        Review review = findReviewOrThrow(reviewId);
        if (!reviewStorage.likeExists(reviewId, userId)) {
            log.warn("Пользователь с id={} не поставил лайк перед тем как его убрать на отзыв с id={}",
                    userId, reviewId);
            throw new InvalidUserInputException("Пользователь еще не поставил лайк на отзыв с id=" + reviewId);
        }
        review.setUseful(review.getUseful() + DISLIKE_VALUE);
        reviewStorage.removeLike(reviewId, userId, review.getUseful());
        return review;
    }

    @Transactional
    public Review removeDislike(long reviewId, long userId) {
        checkUserExistence(userId);
        Review review = findReviewOrThrow(reviewId);
        if (!reviewStorage.dislikeExists(reviewId, userId)) {
            log.warn("Пользователь с id={} не поставил дизлайк перед тем как его убрать на отзыв с id={}",
                    userId, reviewId);
            throw new InvalidUserInputException("Пользователь еще не поставил дизлайк на отзыв с id=" + reviewId);
        }
        review.setUseful(review.getUseful() + LIKE_VALUE);
        reviewStorage.removeDislike(reviewId, userId, review.getUseful());
        return review;
    }

    private void checkUserExistence(long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Пользователь с id={} не найден.", userId);
            throw new NotFoundException("Нет пользователя с id=" + userId);
        }
    }

    private Review findReviewOrThrow(long id) {
        Optional<Review> optionalReview = reviewStorage.findById(id);
        if (optionalReview.isEmpty()) {
            log.warn("Отзыв с id={} не найден.", id);
            throw new NotFoundException("Отзыв с id=" + id + " не найден.");
        }
        return optionalReview.get();
    }
}

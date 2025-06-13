package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    public Collection<Review> findAll() {
        return reviewStorage.findAll();
    }

    public Review create(Review review) {
        if (filmStorage.findFilmById(review.getFilmId()) == null) {
            log.warn("Фильм с id={} не найден", review.getFilmId());
            throw new NotFoundException("Фильм с id=" + review.getFilmId() + " не найден.");
        }
        if (userStorage.getUserById(review.getUserId()).isEmpty()) {
            log.warn("Пользователь с id={} не найден.", review.getUserId());
            throw new NotFoundException("Пользователь с id=" + review.getUserId() + " не найден.");
        }
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

    private Review findReviewOrThrow(long id) {
        Optional<Review> optionalReview = reviewStorage.findById(id);
        if (optionalReview.isEmpty()) {
            log.warn("Отзыв с id={} не найден.", id);
            throw new NotFoundException("Отзыв с id=" + id + " не найден.");
        }
        return optionalReview.get();
    }
}

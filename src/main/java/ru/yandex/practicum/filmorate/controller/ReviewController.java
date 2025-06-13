package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody Review review) {
        Review createdReview = reviewService.create(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @PutMapping
    public ResponseEntity<Review> update(@Valid @RequestBody Review review) {
        Review updatedReview = reviewService.update(review);
        return ResponseEntity.status(HttpStatus.OK).body(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        reviewService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> findById(@PathVariable @Min(1) Long id) {
        Review review = reviewService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(review);
    }

    @GetMapping
    public ResponseEntity<Collection<Review>> findCountReviewsByFilmId(
            @RequestParam Long filmId,
            @RequestParam(defaultValue = "10") int count) {
        Collection<Review> reviews = reviewService.findCountReviewsByFilmId(filmId, count);
        return ResponseEntity.ok().body(reviews);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> likeReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        Review review = reviewService.likeReview(reviewId, userId);
        return ResponseEntity.ok().body(review);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> dislikeReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        Review review = reviewService.dislikeReview(reviewId, userId);
        return ResponseEntity.ok().body(review);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> removeLike(@PathVariable("id") long reviewId, @PathVariable long userId) {
        Review review = reviewService.dislikeReview(reviewId, userId);
        return ResponseEntity.ok().body(review);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> removeDislike(@PathVariable("id") long reviewId, @PathVariable long userId) {
        Review review = reviewService.likeReview(reviewId, userId);
        return ResponseEntity.ok().body(review);
    }
}

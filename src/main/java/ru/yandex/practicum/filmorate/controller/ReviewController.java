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

    @GetMapping
    public ResponseEntity<Collection<Review>> findAll() {
        Collection<Review> reviewCollection = reviewService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(reviewCollection);
    }

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
}

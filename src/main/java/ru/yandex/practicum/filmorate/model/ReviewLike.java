package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewLike {
    private Long id;
    private Long reviewId;
    private Long userId;
}

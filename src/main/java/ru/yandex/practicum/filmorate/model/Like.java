package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Like {
    private Long film_id;
    private Long user_id;
}

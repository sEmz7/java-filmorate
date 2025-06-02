package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingRowMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingDbStorage {
    private final JdbcTemplate jdbc;
    private final RatingRowMapper ratingRowMapper;
    private static final String FIND_RATING_BY_ID = "SELECT * FROM ratings WHERE rating_id = ?;";
    private static final String FIND_ALL = "SELECT * FROM ratings;";

    public Rating getRatingById(long id) {
        return jdbc.query(FIND_RATING_BY_ID, ratingRowMapper, id).stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Рейтинг с id=" + id + " не найден."));
    }

    public List<Rating> findAll() {
        return jdbc.query(FIND_ALL, ratingRowMapper);
    }
}

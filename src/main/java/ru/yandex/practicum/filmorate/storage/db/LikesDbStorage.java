package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LikesDbStorage {
    private final JdbcTemplate jdbc;
    private final LikeRowMapper likeRowMapper;
    private static final String FIND_LIKES = "SELECT * FROM likes WHERE film_id = ?;";
    private static final String CREATE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?) ";
    private static final String DELETE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";

    public List<Like> findFilmLikes(long filmId) {
        return jdbc.query(FIND_LIKES, likeRowMapper, filmId);
    }

    public void addLike(long filmId, long userId) {
        jdbc.update(CREATE, filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        jdbc.update(DELETE, filmId, userId);
    }
}

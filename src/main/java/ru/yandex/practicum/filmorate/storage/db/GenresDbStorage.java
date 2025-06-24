package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GenresDbStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreRowMapper;
    private static final String SAVE_FILM_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?);";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?;";
    private static final String FIND_FILM_GENRES = "SELECT g.genre_id, g.name " +
            "FROM film_genres AS fg " +
            "JOIN genres AS g ON g.genre_id = fg.genre_id " +
            "WHERE film_id = ?;";
    private static final String FIND_ALL = "SELECT * FROM genres";
    private static final String FIND_ALL_WITH_FILM_ID = "SELECT fg.film_id, g.genre_id, g.name " +
            "FROM film_genres fg JOIN genres g ON fg.genre_id = g.genre_id";

    public Map<Long, List<Genre>> findAllFilmGenres() {
        return jdbc.query(FIND_ALL_WITH_FILM_ID, rs -> {
            Map<Long, List<Genre>> result = new HashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("film_id");
                Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("name"));
                result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
            }
            return result;
        });
    }

    public void saveFilmGenres(long filmId, List<Genre> genres) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        for (Genre genre : genres) {
            getGenreById(genre.getId());
            jdbc.update(SAVE_FILM_GENRES, filmId, genre.getId());
        }
    }

    public Genre getGenreById(long id) {
        return jdbc.query(FIND_GENRE_BY_ID, genreRowMapper, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Нету жанра с id=" + id));
    }

    public List<Genre> findAll() {
        return jdbc.query(FIND_ALL, genreRowMapper);
    }

    public List<Genre> findFilmGenres(long filmId) {
        return jdbc.query(FIND_FILM_GENRES, genreRowMapper, filmId);
    }
}

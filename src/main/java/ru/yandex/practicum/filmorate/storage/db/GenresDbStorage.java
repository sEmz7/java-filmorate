package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenresDbStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreRowMapper;
    private static final String SAVE_FILM_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?);";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?;";
    private static final String FIND_ALL = "SELECT * FROM genres";

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
}

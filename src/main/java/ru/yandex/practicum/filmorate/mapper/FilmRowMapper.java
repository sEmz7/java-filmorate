package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Rating(rs.getLong("rating_id"), rs.getString("rating_name")))
                .genres(new ArrayList<>())
                .director(new Director(rs.getLong("director_id"), rs.getString("director_name")))
                .build();

        long genreId = rs.getLong("genre_id");
        if (!rs.wasNull()) {
            film.getGenres().add(new Genre(genreId, rs.getString("genre_name")));
        }

        return film;
    }
}


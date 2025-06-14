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
                .directors(new ArrayList<>())
                .build();

        long genreId = rs.getLong("genre_id");
        String genre = rs.getString("genre_name");
        if (genre != null) {
            film.getGenres().add(new Genre(genreId, genre));
        }

        long directorId = rs.getLong("director_id");
        String director = rs.getString("director_name");
        if (director != null) {
            film.getDirectors().add(new Director(directorId, director));
        }

        return film;
    }
}


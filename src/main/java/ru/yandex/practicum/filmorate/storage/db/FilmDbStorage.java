package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.InvalidFilmInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Component("filmDb")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;
    private final GenresDbStorage genresDbStorage;
    private final RatingDbStorage ratingDbStorage;
    private final LikesDbStorage likesDbStorage;
    private static final String FIND_ALL = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.rating_id, r.name AS rating_name " +
            "FROM films AS f " +
            "JOIN ratings AS r ON f.rating_id = r.rating_id ";
    private static final String CREATE_FILM =
            "INSERT INTO films(name, description, release_date, duration, rating_id) " + "VALUES (?, ?, ?, ?, ?);";
    private static final String FIND_BY_ID =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, r.name AS rating_name, " +
                    "g.genre_id, gr.name AS genre_name " +
                    "FROM films AS f " +
                    "JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
                    "LEFT JOIN genres AS gr ON g.genre_id = gr.genre_id " +
                    "WHERE f.id = ?";
    private static final String UPDATE =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?;";


    @Override
    public Collection<Film> findAll() {
        return jdbc.query(FIND_ALL, (rs, rowNum) -> getFilmFromResultSet(rs));
    }

    @Override
    public Film create(Film film) {
        Rating rating = ratingDbStorage.getRatingById(film.getMpa().getId());
        Long id = insert(CREATE_FILM,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), rating.getId());
        film.setId(id);
        if (film.getGenres() != null) {
            genresDbStorage.saveFilmGenres(film.getId(), film.getGenres());
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        findFilmById(film.getId());
        jdbc.update(UPDATE,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        if (film.getGenres() != null) {
            genresDbStorage.saveFilmGenres(film.getId(), film.getGenres());
        }
        if (film.getMpa() != null) {
            ratingDbStorage.getRatingById(film.getMpa().getId());
        }
        return film;
    }

    @Override
    public Film delete(long id) {
        return null;
    }

    @Override
    public Film findFilmById(long id) {
        List<Film> partialFilms = jdbc.query(FIND_BY_ID, filmRowMapper, id);

        if (partialFilms.isEmpty()) {
            throw new NotFoundException("Нет фильма с id=" + id);
        }

        Film mergedFilm = partialFilms.get(0);
        Set<Genre> genreSet = new LinkedHashSet<>();
        for (Film film : partialFilms) {
            genreSet.addAll(film.getGenres());
        }
        mergedFilm.setGenres(new ArrayList<>(genreSet));

        return mergedFilm;
    }


    private long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public List<Like> findFilmLikes(long id) {
        return likesDbStorage.findFilmLikes(id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        likesDbStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        likesDbStorage.deleteLike(filmId, userId);
    }

    private Film getFilmFromResultSet(ResultSet resultSet) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
    }
}

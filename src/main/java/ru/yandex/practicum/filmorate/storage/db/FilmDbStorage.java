package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("filmDb")
public class FilmDbStorage extends BaseDbStorage implements FilmStorage {
    private final FilmRowMapper filmRowMapper;
    private final GenresDbStorage genresDbStorage;
    private final RatingDbStorage ratingDbStorage;
    private final LikesDbStorage likesDbStorage;
    private final DirectorsDbStorage directorsDbStorage;

    private static final String FIND_ALL = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.rating_id, r.name AS rating_name " +
            "FROM films AS f " +
            "INNER JOIN ratings AS r ON f.rating_id = r.rating_id;";
    private static final String CREATE_FILM =
            "INSERT INTO films(name, description, release_date, duration, rating_id) " + "VALUES (?, ?, ?, ?, ?);";
    private static final String FIND_BY_ID =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, r.name AS rating_name, " +
                    "g.genre_id, gr.name AS genre_name, " +
                    "fd.director_id, d.name AS director_name " +
                    "FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
                    "LEFT JOIN genres AS gr ON g.genre_id = gr.genre_id " +
                    "LEFT JOIN film_directors AS fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "WHERE f.id = ?;";
    private static final String UPDATE =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?;";
    private static final String DELETE = "DELETE FROM films WHERE id = ?;";
    private static final String FIND_BY_DIRECTOR_SORT_BY_YEAR =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, r.name AS rating_name, " +
                    "g.genre_id, gr.name AS genre_name, " +
                    "fd.director_id, d.name AS director_name " +
                    "FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
                    "LEFT JOIN genres AS gr ON g.genre_id = gr.genre_id " +
                    "LEFT JOIN film_directors AS fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "WHERE d.director_id = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY YEAR(f.release_date);";
    private static final String FIND_BY_DIRECTOR_SORT_BY_LIKES =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, r.name AS rating_name, " +
                    "g.genre_id, gr.name AS genre_name, " +
                    "fd.director_id, d.name AS director_name " +
                    "FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
                    "LEFT JOIN genres AS gr ON g.genre_id = gr.genre_id " +
                    "LEFT JOIN film_directors AS fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "LEFT JOIN likes AS l ON f.id = l.film_id " +
                    "WHERE d.director_id = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.id) DESC;";
    private static final String FIND_COMMON_FILMS =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, r.name AS rating_name, " +
                    "g.genre_id, gr.name AS genre_name, " +
                    "fd.director_id, d.name AS director_name " +
                    "FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
                    "LEFT JOIN genres AS gr ON g.genre_id = gr.genre_id " +
                    "LEFT JOIN film_directors AS fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "INNER JOIN likes AS l1 ON f.id = l1.film_id AND l1.user_id = ? " +
                    "INNER JOIN likes AS l2 ON f.id = l2.film_id AND l2.user_id = ? " +
                    "GROUP BY f.id, g.genre_id, gr.name, fd.director_id, d.name;";
    private static final String FIND_TOP_FILMS_BY_GENRE_AND_YEAR =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, r.name AS rating_name, " +
                    "g.genre_id, gr.name AS genre_name, " +
                    "fd.director_id, d.name AS director_name " +
                    "FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
                    "LEFT JOIN genres AS gr ON g.genre_id = gr.genre_id " +
                    "LEFT JOIN film_directors AS fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "LEFT JOIN likes AS l ON f.id = l.film_id " +
                    "WHERE (? IS NULL OR g.genre_id = ?) " +
                    "AND (? IS NULL OR EXTRACT(YEAR FROM f.release_date) = ?) " +
                    "GROUP BY f.id, g.genre_id, gr.name, fd.director_id, d.name " +
                    "ORDER BY COUNT(l.id) DESC " +
                    "LIMIT ?";

    private static final String SEARCH_BY_DIRECTOR =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, r.name AS rating_name, " +
                    "g.genre_id, gr.name AS genre_name, " +
                    "fd.director_id, d.name AS director_name " +
                    "FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
                    "LEFT JOIN genres AS gr ON g.genre_id = gr.genre_id " +
                    "LEFT JOIN film_directors AS fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "LEFT JOIN likes AS l ON f.id = l.film_id " +
                    "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%')) " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.id) DESC";
    private static final String SEARCH_BY_NAME =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, r.name AS rating_name, " +
                    "g.genre_id, gr.name AS genre_name, " +
                    "fd.director_id, d.name AS director_name " +
                    "FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
                    "LEFT JOIN genres AS gr ON g.genre_id = gr.genre_id " +
                    "LEFT JOIN film_directors AS fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "LEFT JOIN likes AS l ON f.id = l.film_id " +
                    "WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%')) " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.id) DESC";
    private static final String SEARCH_BY_DIRECTOR_AND_NAME =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                    "f.rating_id, r.name AS rating_name, " +
                    "g.genre_id, gr.name AS genre_name, " +
                    "fd.director_id, d.name AS director_name " +
                    "FROM films AS f " +
                    "INNER JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "LEFT JOIN film_genres AS g ON f.id = g.film_id " +
                    "LEFT JOIN genres AS gr ON g.genre_id = gr.genre_id " +
                    "LEFT JOIN film_directors AS fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "LEFT JOIN likes AS l ON f.id = l.film_id " +
                    "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%')) OR " +
                    "LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%')) " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.id) DESC";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmRowMapper, GenresDbStorage genresDbStorage, RatingDbStorage ratingDbStorage, LikesDbStorage likesDbStorage, DirectorsDbStorage directorsDbStorage) {
        super(jdbc);
        this.filmRowMapper = filmRowMapper;
        this.genresDbStorage = genresDbStorage;
        this.ratingDbStorage = ratingDbStorage;
        this.likesDbStorage = likesDbStorage;
        this.directorsDbStorage = directorsDbStorage;
    }

    @Override
    public List<Film> findAll() {
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
        if (film.getDirectors() != null) {
            directorsDbStorage.saveFilmDirectors(film.getId(), film.getDirectors());
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
        if (film.getDirectors() != null) {
            directorsDbStorage.saveFilmDirectors(film.getId(), film.getDirectors());
        }
        return film;
    }

    @Override
    public Film delete(long filmId) {
        Film film = findFilmById(filmId);
        if (film == null) {
            log.warn("Фильм с id={} не найден.", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
        jdbc.update(DELETE, filmId);
        return film;
    }

    @Override
    public Film findFilmById(long id) {
        List<Film> partialFilms = jdbc.query(FIND_BY_ID, filmRowMapper, id);

        if (partialFilms.isEmpty()) {
            throw new NotFoundException("Нет фильма с id=" + id);
        }

        Film mergedFilm = partialFilms.getFirst();
        Set<Genre> genreSet = new LinkedHashSet<>();
        for (Film film : partialFilms) {
            genreSet.addAll(film.getGenres());
        }
        mergedFilm.setGenres(new ArrayList<>(genreSet));

        return mergedFilm;
    }

    @Override
    public List<Film> findFilmsByDirectorSortYear(long id) {
        return jdbc.query(FIND_BY_DIRECTOR_SORT_BY_YEAR, filmRowMapper, id);
    }

    @Override
    public List<Film> findFilmsByDirectorSortLikes(long id) {
        return jdbc.query(FIND_BY_DIRECTOR_SORT_BY_LIKES, filmRowMapper, id);
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

    @Override
    public List<Film> search(String query, List<String> by) {
        if (by.size() == 2) {
            return jdbc.query(SEARCH_BY_DIRECTOR_AND_NAME, filmRowMapper, query, query);
        } else {
            return by.getFirst().equals("director") ?
                    jdbc.query(SEARCH_BY_DIRECTOR, filmRowMapper, query) :
                    jdbc.query(SEARCH_BY_NAME, filmRowMapper, query);
        }
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

    @Override
    public List<Film> findCommonFilms(long userId, long friendId) {
        List<Film> partialFilms = jdbc.query(FIND_COMMON_FILMS, filmRowMapper, userId, friendId);
        return processFilms(partialFilms);
    }

    @Override
    public List<Film> findTopFilmsByGenreAndYear(int limit, Long genreId, Integer year) {
        List<Film> partialFilms = jdbc.query(
                FIND_TOP_FILMS_BY_GENRE_AND_YEAR,
                filmRowMapper,
                genreId, genreId,
                year, year,
                limit
        );
        return processFilms(partialFilms);
    }

    private List<Film> processFilms(List<Film> partialFilms) {
        Map<Long, Film> filmMap = new LinkedHashMap<>();

        for (Film film : partialFilms) {
            filmMap.computeIfAbsent(film.getId(), id -> createBasicFilm(film));

            if (!film.getGenres().isEmpty()) {
                filmMap.get(film.getId()).getGenres().addAll(film.getGenres());
            }
            if (!film.getDirectors().isEmpty()) {
                filmMap.get(film.getId()).getDirectors().addAll(film.getDirectors());
            }
        }

        for (Film film : filmMap.values()) {
            addLikesToFilm(film);
        }

        return new ArrayList<>(filmMap.values());
    }

    private Film createBasicFilm(Film film) {
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .likes(new HashSet<>())
                .build();
    }

    private void addLikesToFilm(Film film) {
        List<Like> likes = likesDbStorage.findFilmLikes(film.getId());
        film.getLikes().addAll(likes.stream()
                .map(Like::getUserId)
                .collect(Collectors.toSet()));
    }
}

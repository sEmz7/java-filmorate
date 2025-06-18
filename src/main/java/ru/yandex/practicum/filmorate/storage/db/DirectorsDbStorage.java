package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Component
@Slf4j
public class DirectorsDbStorage extends BaseDbStorage {
    private final DirectorRowMapper directorRowMapper;
    private static final String SAVE_DIRECTOR = "INSERT INTO directors (name) VALUES (?);";
    private static final String SAVE_FILM_DIRECTORS = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?);";
    private static final String UPDATE_DIRECTOR = "UPDATE directors SET name = ? WHERE director_id = ?;";
    private static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?;";
    private static final String FIND_ALL = "SELECT * FROM directors";
    private static final String FIND_BY_FILM_ID = "SELECT d.director_id, d.name FROM film_directors AS fd " +
                                                    "JOIN directors AS d ON d.director_id = fd.director_id " +
                                                    "WHERE fd.film_id = ?;";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";
    private static final String DELETE_FILM_DIRECTORS = "DELETE FROM film_directors WHERE film_id = ?";

    @Autowired
    public DirectorsDbStorage(JdbcTemplate jdbc, DirectorRowMapper directorRowMapper) {
        super(jdbc);
        this.directorRowMapper = directorRowMapper;
    }

    public void saveFilmDirectors(long filmId, List<Director> directors) {
        for (Director director : directors) {
            findById(director.getId());
            jdbc.update(SAVE_FILM_DIRECTORS, filmId, director.getId());
        }
    }

    public Director create(Director director) {
        Long id = insert(SAVE_DIRECTOR, director.getName());
        director.setId(id);
        return director;
    }

    public Director update(Director director) {
        jdbc.update(UPDATE_DIRECTOR, director.getName(), director.getId());
        return director;
    }

    public Director findById(long id) {
        return jdbc.query(FIND_DIRECTOR_BY_ID, directorRowMapper, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Нет директора с id=" + id));
    }

    public List<Director> findAll() {
        return jdbc.query(FIND_ALL, directorRowMapper);
    }

    public List<Director> findByFilmId(long filmId) {
        return jdbc.query(FIND_BY_FILM_ID, directorRowMapper, filmId);
    }

    public Director delete(long directorId) {
        Director director = findById(directorId);
        if (director == null) {
            log.warn("Директор с id={} не найден.", directorId);
            throw new NotFoundException("Директор с id=" + directorId + " не найден.");
        }
        jdbc.update(DELETE_DIRECTOR, directorId);
        return director;
    }

    public void deleteFilmDirectors(long filmId) {
        jdbc.update(DELETE_FILM_DIRECTORS, filmId);
    }
}

package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorsDbStorage {
    private final JdbcTemplate jdbc;
    private final DirectorRowMapper directorRowMapper;
    private static final String SAVE_DIRECTOR = "INSERT INTO directors (name) VALUES (?);";
    private static final String SAVE_FILM_DIRECTORS = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?);";
    private static final String UPDATE_DIRECTOR = "UPDATE directors SET name = ? WHERE director_id = ?;";
    private static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?;";
    private static final String FIND_ALL = "SELECT * FROM directors";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";
    private static final String DELETE_FILM_DIRECTORS = "DELETE FROM film_directors WHERE film_id = ?";

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
        }
        throw new InternalServerException("Не удалось сохранить данные");
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

    public Director delete(long directorId) {
        Director director = findById(directorId);
        if (director == null) {
            log.warn("Директор с id={} не найден.", directorId);
            throw new NotFoundException("Директор с id=" + directorId + " не найден.");
        }
        jdbc.update(DELETE_DIRECTOR, directorId);
        return director;
    }
}

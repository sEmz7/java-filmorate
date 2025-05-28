package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Component("userDb")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> userRowMapper;
    private static final String FIND_ALL = "SELECT * FROM users;";
    private static final String CREATE_USER =
            "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER =
            "UPDATE users SET name = ?, login = ?, email = ?, birthday = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?";


    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> userRowMapper) {
        this.jdbc = jdbc;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public Collection<User> findAll() {
        return jdbc.query(FIND_ALL, userRowMapper);
    }

    @Override
    public User create(User user) {
        Long id = insert(CREATE_USER, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        Optional<User> optionalUser = getUserById(user.getId());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        jdbc.update(UPDATE_USER, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User delete(long id) {
        Optional<User> optionalUser = getUserById(id);
        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с id={} не найден", id);
            throw new NotFoundException("Пользователь не найден.");
        }
        jdbc.update(DELETE_USER, id);
        return optionalUser.get();
    }

    @Override
    public Optional<User> getUserById(long id) {
        return jdbc.query(FIND_BY_ID, userRowMapper, id).stream().findFirst();
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
}

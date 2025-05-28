package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendDbStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> userRowMapper;
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    private static final String FIND_USER_FRIENDS = "SELECT u.id, u.name, u.login, u.email, u.birthday " +
            "FROM friends AS f " +
            "JOIN users AS u ON u.id = f.friend_id " +
            "WHERE f.user_id = ? " +
            "ORDER BY u.id";
    private static final String DELETE_USER_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_COMMON_FRIENDS = "";

    public void addFriend(long userId, long friendId) {
        jdbc.update(ADD_FRIEND_QUERY, userId, friendId);
    }

    public List<User> getUserFriends(long userId) {
        return jdbc.query(FIND_USER_FRIENDS, userRowMapper, userId);
    }

    public void deleteFriend(long userId, long friendId) {
        jdbc.update(DELETE_USER_FRIEND, userId, friendId);
    }
}

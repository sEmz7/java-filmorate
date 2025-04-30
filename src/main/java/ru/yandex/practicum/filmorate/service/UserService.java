package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.InvalidUserInputException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    public User update(@RequestBody User user) {
        return userStorage.update(user);
    }

    public User addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("userId равен friendId, нельзя добавить в друзья.");
            throw new InvalidUserInputException("Нельзя добавить самого себя в друзья.");
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);
        log.debug("User id={} добавил в друзья User id={}", user.getId(), friend.getId());
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("userId == friendId, нельзя удалить из друзей.");
            throw new InvalidUserInputException("Нельзя удалить самого себя из друзей.");
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);
        log.debug("User id={} удалил из друзей User id={}", user.getId(), friend.getId());
        return user;
    }

    public Collection<User> findAllFriends(long userId) {
        User user = userStorage.getUserById(userId);
        Set<Long> userFriendsId = user.getFriends();
        log.debug("Возвращены друзья пользователя с id={}", user.getId());
        return userFriendsId.stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public Collection<User> findAllCommonFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(friend.getFriends());
        log.debug("Возвращены совместные друзья пользователей с id={} и id={}", user.getId(), friend.getId());
        return commonFriendIds.stream()
                .map(userStorage::getUserById)
                .toList();
    }
}
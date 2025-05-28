package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.InvalidUserInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.FriendDbStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendDbStorage friendStorage;

    @Autowired
    public UserService(@Qualifier(value = "userDb") UserStorage userStorage, FriendDbStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(@Valid @RequestBody User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new InvalidUserInputException("Дата рождения не может быть позже сегодняшнего дня.");
        }
        return userStorage.create(user);
    }

    public User update(@RequestBody User user) {
        return userStorage.update(user);
    }

    public User delete(long id) {
        return userStorage.delete(id);
    }

    public User getUser(long id) {
        Optional<User> optionalUser = userStorage.getUserById(id);
        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с id={} не найден.", id);
            throw new NotFoundException("Пользователь не найден.");
        }
        return optionalUser.get();
    }

    public Collection<User> findAllFriends(long userId) {
        User user = getUserByIdOrThrow(userId);
        return friendStorage.getUserFriends(userId);
    }

    public User addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("userId равен friendId, нельзя добавить в друзья.");
            throw new InvalidUserInputException("Нельзя добавить самого себя в друзья.");
        }
        User user = getUserByIdOrThrow(userId);
        getUserByIdOrThrow(friendId);

        friendStorage.addFriend(userId, friendId);
        log.debug("Пользователь с id={} добавил в друзья пользователя с id={}", userId, friendId);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("userId == friendId, нельзя удалить из друзей.");
            throw new InvalidUserInputException("Нельзя удалить самого себя из друзей.");
        }
        User user = getUserByIdOrThrow(userId);
        getUserByIdOrThrow(friendId);
        friendStorage.deleteFriend(userId, friendId);

        log.debug("User id={} удалил из друзей User id={}", userId, friendId);
        return user;
    }

    private User getUserByIdOrThrow(long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден."));
    }

    public List<User> findAllCommonFriends(long userId, long friendId) {
        User user = getUserByIdOrThrow(userId);
        User friend = getUserByIdOrThrow(friendId);

        List<User> userFriends = friendStorage.getUserFriends(userId);
        List<User> friendFriends = friendStorage.getUserFriends(friendId);

        Set<Long> friendIds1 = userFriends.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        Set<Long> friendIds2 = friendFriends.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        friendIds1.retainAll(friendIds2);
        log.debug("Возвращены совместные друзья пользователей с id={} и id={}", user.getId(), friend.getId());
        return friendIds1.stream()
                .map(this::getUserByIdOrThrow)
                .toList();
    }
}
package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.InvalidUserInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
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
            throw new InvalidUserInputException("Нельзя добавить самого себя в друзья.");
        }

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);

        return user;
    }



    public User deleteFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new InvalidUserInputException("Нельзя удалить самого себя из друзей.");
        }

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);

        return user;
    }

    public Collection<User> findAllFriends(long userId) {
        User user = getUserById(userId);
        Set<Long> userFriendsId = user.getFriends();
        return userStorage.findAll().stream()
                .filter(user1 -> userFriendsId.contains(user1.getId()))
                .toList();
    }

    public Collection<User> findAllCommonFriends(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(friend.getFriends());

        return commonFriendIds.stream()
                .map(this::getUserById)
                .toList();
    }

    private User getUserById(long id) {
        return userStorage.findAll().stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с Id=" + id +" не найден."));
    }
}
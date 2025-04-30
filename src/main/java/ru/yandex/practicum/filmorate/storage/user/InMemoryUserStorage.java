package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InvalidUserInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> reservedEmails = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        if (reservedEmails.containsKey(user.getEmail())) {
            log.warn("email '{}' уже используется", user.getEmail());
            throw new InvalidUserInputException("Этот email уже используется");
        }
        validateUser(user);
        user.setId(getNextId());
        log.trace("Пользователю '{}' присвоен id={}", user.getLogin(), user.getId());
        user.setFriends(new HashSet<>());
        reservedEmails.put(user.getEmail(), user);
        users.put(user.getId(), user);
        log.debug("User успешно добавлен");
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            log.warn("Не указан ID пользователя");
            throw new InvalidUserInputException("Id должен быть указан");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с id={} не был найден", user.getId());
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        if (reservedEmails.containsKey(user.getEmail()) && reservedEmails.get(user.getEmail()) != user) {
            log.warn("email '{}' уже используется", user.getEmail());
            throw new InvalidUserInputException("Этот email уже используется");
        }
        validateUser(user);
        users.put(user.getId(), user);
        log.debug("User успешно обновлен");
        return user;
    }

    @Override
    public User delete(long id) {
        reservedEmails.remove(getUserById(id).getEmail());
        return users.remove(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUser(User newUser) {
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения должна быть раньше сегодняшней даты. Введенная дата '{}'", newUser.getBirthday());
            throw new InvalidUserInputException("Дата рождения должна быть раньше сегодняшней даты.");
        }
        if (newUser.getLogin().contains(" ")) {
            log.warn("Логин '{}' содержит пробел", newUser.getLogin());
            throw new InvalidUserInputException("Логин не может содержать пробелы.");
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.trace("Имя пользователя '{}' заменено на его логин", newUser.getLogin());
        }
    }

    public User getUserById(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с Id=" + id + " не найден.");
        }
        return user;
    }
}

package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidUserInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User newUser) {
        validateUser(newUser);
        newUser.setId(getNextId());
        log.trace("Пользователю '{}' присвоен id={}", newUser.getLogin(), newUser.getId());
        users.put(newUser.getId(), newUser);
        log.debug("User успешно добавлен");
        return newUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Не указан ID пользователя");
            throw new InvalidUserInputException("Id должен быть указан");
        }
        if (!users.containsKey(newUser.getId())) {
            log.warn("Пользователь с id={} не был найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
        validateUser(newUser);
        users.put(newUser.getId(), newUser);
        log.debug("User успешно обновлен");
        return newUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void validateUser(User newUser) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
            log.warn("email '{}' уже используется", newUser.getEmail());
            throw new InvalidUserInputException("Этот email уже используется");
        }
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
}

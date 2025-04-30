package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.InvalidUserInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static UserController userController;

    @BeforeEach
    void setup() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void addUserTest() {
        User user1 = User.builder()
                .email("email@mail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        userController.create(user1);
        user1.setName("aawdawd");

        User userWithInvalidDate = User.builder()
                .email("ddd@mail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(9999, 10, 10))
                .build();

        User userWithSpaceInLogin = User.builder()
                .email("bbb@mail.ru")
                .login("login ddd")
                .name("name")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();


        assertThrows(InvalidUserInputException.class, () -> userController.create(user1));
        assertThrows(InvalidUserInputException.class, () -> userController.create(userWithInvalidDate));
        assertThrows(InvalidUserInputException.class, () -> userController.create(userWithSpaceInLogin));
    }

    @Test
    void updateUserTest() {
        User user1 = User.builder()
                .id(1L)
                .email("ddd@mail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 10, 10))
                .build();

        assertThrows(NotFoundException.class, () -> userController.update(user1));

        userController.create(user1);
        user1.setBirthday(LocalDate.of(9999, 12,12));

        assertThrows(InvalidUserInputException.class, () -> userController.update(user1));

        User user2 = User.builder()
                .id(1L)
                .email("bbb@mail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        userController.create(user2);
        user2.setLogin("login space");

        assertThrows(InvalidUserInputException.class, () -> userController.update(user2));
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.InvalidUserInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static UserController userController;

    @BeforeEach
    void setup() {
        userController = new UserController();
    }

    @Test
    void addUserTest() {
        User user1 = User.builder()
                .email("email@mail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        userController.addUser(user1);
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


        assertThrows(InvalidUserInputException.class, () -> userController.addUser(user1));
        assertThrows(InvalidUserInputException.class, () -> userController.addUser(userWithInvalidDate));
        assertThrows(InvalidUserInputException.class, () -> userController.addUser(userWithSpaceInLogin));
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

        assertThrows(NotFoundException.class, () -> userController.updateUser(user1));

        userController.addUser(user1);
        user1.setBirthday(LocalDate.of(9999, 12,12));

        assertThrows(InvalidUserInputException.class, () -> userController.updateUser(user1));

        User user2 = User.builder()
                .id(1L)
                .email("bbb@mail.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2002, 10, 10))
                .build();
        userController.addUser(user2);
        user2.setLogin("login space");

        assertThrows(InvalidUserInputException.class, () -> userController.updateUser(user2));
    }
}
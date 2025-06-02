package ru.yandex.practicum.filmorate.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void testFindUserById() {
        Optional<User> userOptional = userStorage.getUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    void testCreateUser() {
        User newUser = User.builder()
                .email("test@example.com")
                .login("testlogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userStorage.create(newUser);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(createdUser.getLogin()).isEqualTo(newUser.getLogin());
        assertThat(createdUser.getName()).isEqualTo(newUser.getName());
        assertThat(createdUser.getBirthday()).isEqualTo(newUser.getBirthday());

        Optional<User> userOptional = userStorage.getUserById(createdUser.getId());
        assertThat(userOptional).isPresent();
        assertThat(userOptional.get().getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    void testUpdateUser() {
        User user = User.builder()
                .email("original@example.com")
                .login("originallogin")
                .name("Original Name")
                .birthday(LocalDate.of(1980, 5, 5))
                .build();

        User createdUser = userStorage.create(user);

        createdUser.setEmail("updated@example.com");
        createdUser.setName("Updated Name");

        User updatedUser = userStorage.update(createdUser);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(createdUser.getId());
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getLogin()).isEqualTo("originallogin");

        Optional<User> userOptional = userStorage.getUserById(updatedUser.getId());
        assertThat(userOptional).isPresent();
        assertThat(userOptional.get().getEmail()).isEqualTo("updated@example.com");
        assertThat(userOptional.get().getName()).isEqualTo("Updated Name");
    }
}

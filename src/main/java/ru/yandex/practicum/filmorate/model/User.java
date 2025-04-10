package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"email", "id"})
public class User {
    private Long id;

    @NotNull
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String login;

    private String name;

    @NotNull
    private LocalDate birthday;
}

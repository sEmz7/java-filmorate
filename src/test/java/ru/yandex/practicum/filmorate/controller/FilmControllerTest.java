package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.InvalidFilmInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    private static FilmController filmController;

    @BeforeEach
    void setup() {
        filmController = new FilmController();
    }

    @Test
    void getAllFilmsTest() {
        Film film1 = Film.builder()
                .id(1L)
                .name("name")
                .description("Desc")
                .releaseDate(LocalDate.of(2022, 12, 12))
                .duration(144)
                .build();
        Film film2 = Film.builder()
                .id(2L)
                .name("name2")
                .description("Desc2")
                .releaseDate(LocalDate.of(2012, 2, 12))
                .duration(113)
                .build();
        filmController.addFilm(film1);
        filmController.addFilm(film2);

        assertEquals(2, filmController.getAllFilms().size());
    }

    @Test
    void addFilmTest() {
        Film filmInvalidDate = Film.builder()
                .id(1L)
                .name("name")
                .description("Desc")
                .releaseDate(LocalDate.of(1700, 12, 12))
                .duration(144)
                .build();

        assertThrows(InvalidFilmInputException.class, () -> filmController.addFilm(filmInvalidDate));
    }

    @Test
    void updateFilmTest() {
        Film film = Film.builder()
                .id(1L)
                .name("name")
                .description("Desc")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(144)
                .build();
        filmController.addFilm(film);
        film.setReleaseDate(LocalDate.of(1700, 10, 10));

        Film film2 = Film.builder()
                .id(999999999L)
                .name("name")
                .description("Desc")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(144)
                .build();

        assertThrows(InvalidFilmInputException.class, () -> filmController.updateFilm(film));
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(film2));
    }
}
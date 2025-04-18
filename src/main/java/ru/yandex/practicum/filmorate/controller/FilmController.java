package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidFilmInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validateFilmDate(film);
        film.setId(getNextId());
        log.trace("Фильму {} присвоен id={}", film.getName(), film.getId());
        films.put(film.getId(), film);
        log.debug("Фильм {} успешно добавлен", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null || !films.containsKey(newFilm.getId())) {
            log.warn("Нет фильма с id={}", newFilm.getId());
            throw new NotFoundException("Нет фильма с таким id.");
        }
        validateFilmDate(newFilm);
        films.put(newFilm.getId(), newFilm);
        log.debug("Фильм {} успешно обновлен", newFilm.getName());
        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void validateFilmDate(Film newFilm) {
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата {} фильма {} раньше 1895.12.28", newFilm.getReleaseDate(), newFilm.getName());
            throw new InvalidFilmInputException("Дата фильма должна быть после 1895.12.28");
        }
    }
}

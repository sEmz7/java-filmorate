package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.InvalidUserInputException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    public Film update(@Valid @RequestBody Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Film addLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().add(user.getId());
        log.debug("User id={} поставил лайк фильму с id={}", user.getId(), film.getId());
        return filmStorage.update(film);
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().remove(user.getId());
        log.debug("User id={} удалил лайк у фильма с id={}", user.getId(), film.getId());
        return filmStorage.update(film);
    }

    public List<Film> findBestByLikes(int count) {
        if (count <= 0) {
            log.warn("Параметр count меньше нуля.");
            throw new InvalidUserInputException("Параметр count должен быть положительным числом");
        }

        List<Film> allFilms = new ArrayList<>(filmStorage.findAll());
        allFilms.sort(Comparator.comparingInt(film -> -film.getLikes().size()));
        log.debug("Возвращен список из лучших фильмов.");
        return allFilms.subList(0, Math.min(count, allFilms.size()));
    }
}

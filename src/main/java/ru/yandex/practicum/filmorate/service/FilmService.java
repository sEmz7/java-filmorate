package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.InvalidFilmInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.DirectorsDbStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorsDbStorage directorsDbStorage;

    @Autowired
    public FilmService(@Qualifier("filmDb") FilmStorage filmStorage,
                       @Qualifier("userDb") UserStorage userStorage,
                       DirectorsDbStorage directorsDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorsDbStorage = directorsDbStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new InvalidFilmInputException("Дата фильма не может быть раньше 1895.12.28");
        }
        return filmStorage.create(film);
    }

    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new InvalidFilmInputException("Укажите id фильму.");
        }
        return filmStorage.update(newFilm);
    }

    public Film delete(long filmId) {
        return filmStorage.delete(filmId);
    }

    public Film addLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с id = " + userId));
        filmStorage.addLike(filmId, userId);
        log.debug("User id={} поставил лайк фильму с id={}", user.getId(), film.getId());
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с id =" + userId));
        filmStorage.deleteLike(filmId, userId);
        log.debug("User id={} удалил лайк у фильма с id={}", user.getId(), film.getId());
        return film;
    }

    public List<Film> findTopFilmsByGenreAndYear(int limit, Long genreId, Integer year) {
        if (limit <= 0) {
            throw new InvalidFilmInputException("Параметр count должен быть положительным");
        }
        return filmStorage.findTopFilmsByGenreAndYear(limit, genreId, year);
    }

    public Film findById(long id) {
        return filmStorage.findFilmById(id);
    }

    public List<Film> findByDirectorAndSort(long directorId, String sortBy) {
        List<Film> films;
        if (sortBy.equals("year")) {
            films = filmStorage.findFilmsByDirectorSortYear(directorId);
        } else if (sortBy.equals("likes")) {
            films = filmStorage.findFilmsByDirectorSortLikes(directorId);
        } else {
            throw new InvalidFilmInputException("Параметр sortBy принимает [year,likes]");
        }

        if (films.isEmpty()) {
            throw new NotFoundException("Нет фильма с director_id=" + directorId);
        }
        return films;
    }

    public List<Film> search(String query, List<String> by) {
        if (by.size() > 2) {
            throw new InvalidFilmInputException("Invalid number of parameters");
        }

        String p1 = "director";
        String p2 = "title";

        if ((!by.getFirst().equals(p1) && !by.getFirst().equals(p2)) ||
                (!by.getLast().equals(p1) && !by.getLast().equals(p2))) {
            throw new InvalidFilmInputException("Invalid parameter " + by.getFirst());
        }

        return filmStorage.search(query, by);
    }

    public List<Film> findCommonFilms(long userId, long friendId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));
        List<Film> commonFilms = filmStorage.findCommonFilms(userId, friendId);
        commonFilms.sort(Comparator.comparingInt((Film film) -> filmStorage.findFilmLikes(film.getId()).size()).reversed());
        log.debug("Найдено {} общих фильмов для пользователей {} и {}", commonFilms.size(), userId, friendId);
        return commonFilms;
    }
}


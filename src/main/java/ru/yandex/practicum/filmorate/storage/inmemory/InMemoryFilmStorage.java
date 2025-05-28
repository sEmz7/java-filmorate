package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InvalidFilmInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
@Deprecated
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        validateFilmDate(film);
        film.setId(getNextId());
        log.trace("Фильму {} присвоен id={}", film.getName(), film.getId());
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        log.debug("Фильм {} успешно добавлен", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("Нет фильма с id={}", film.getId());
            throw new NotFoundException("Нет фильма с таким id.");
        }
        validateFilmDate(film);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
        log.debug("Фильм {} успешно обновлен", film.getName());
        return film;
    }

    @Override
    public Film delete(long id) {
        return films.remove(id);
    }

    @Override
    public Film findFilmById(long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден.");
        }
        return film;
    }

    @Override
    public List<Like> findFilmLikes(long id) {
        return null;
    }

    @Override
    public void addLike(long filmId, long userId) {

    }

    @Override
    public void deleteLike(long filmId, long userId) {

    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateFilmDate(Film newFilm) {
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата {} фильма {} раньше 1895.12.28", newFilm.getReleaseDate(), newFilm.getName());
            throw new InvalidFilmInputException("Дата фильма должна быть после 1895.12.28");
        }
    }
}

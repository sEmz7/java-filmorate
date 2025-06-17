package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film delete(long id);

    Film findFilmById(long id);

    List<Film> findFilmsByDirectorSortYear(long id);

    List<Film> findFilmsByDirectorSortLikes(long id);

    List<Like> findFilmLikes(long id);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> search(String query, List<String> by);

    List<Film> findCommonFilms(long userId, long friendId);

    List<Film> findTopFilmsByGenreAndYear(int limit, Long genreId, Integer year);
}

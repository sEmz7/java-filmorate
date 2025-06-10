package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        return filmService.findById(id);
    }

    @GetMapping("/search")
    public Collection<Film> search(@RequestParam String query, @RequestParam List<String> by) {
        return filmService.search(query, by);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @DeleteMapping("/{filmId}")
    public ResponseEntity<Film> delete(@PathVariable long filmId) {
        Film deletedFilm = filmService.delete(filmId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedFilm);
    }

    @GetMapping("/popular")
    public List<Film> findBestByLikes(@RequestParam(defaultValue = "10") int count) {
        return filmService.findBestByLikes(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        return filmService.deleteLike(filmId, userId);
    }
}

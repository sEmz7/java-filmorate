package ru.yandex.practicum.filmorate.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.db.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({DirectorsDbStorage.class, DirectorRowMapper.class,
        FilmDbStorage.class, FilmRowMapper.class,
        GenresDbStorage.class, GenreRowMapper.class,
        RatingDbStorage.class, RatingRowMapper.class,
        LikesDbStorage.class, LikeRowMapper.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    DirectorsDbStorage directorsStorage;

    @Test
    void testFindFilmById() {
        Director director = directorsStorage.create(new Director("director"));
        Film film = Film.builder()
                .name("test")
                .description("test")
                .releaseDate(LocalDate.of(2005, 1, 1))
                .duration(133)
                .mpa(new Rating(1L, "G"))
                .director(director)
                .build();

        Film createdFilm = filmStorage.create(film);

        Optional<Film> foundFilm = Optional.of(filmStorage.findFilmById(createdFilm.getId()));

        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("id", createdFilm.getId()));
    }


    @Test
    void testCreateFilm() {
        Director director = directorsStorage.create(new Director("director"));
        Film newFilm = Film.builder()
                .name("Inception")
                .description("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpa(new Rating(1L, "G"))
                .director(director)
                .build();

        Film createdFilm = filmStorage.create(newFilm);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo(newFilm.getName());
        assertThat(createdFilm.getDescription()).isEqualTo(newFilm.getDescription());
        assertThat(createdFilm.getReleaseDate()).isEqualTo(newFilm.getReleaseDate());
        assertThat(createdFilm.getDuration()).isEqualTo(newFilm.getDuration());

        Optional<Film> filmOptional = Optional.of(filmStorage.findFilmById(createdFilm.getId()));
        assertThat(filmOptional).isPresent();
        assertThat(filmOptional.get().getName()).isEqualTo(newFilm.getName());
    }

    @Test
    void testUpdateFilm() {
        Director director = directorsStorage.create(new Director("director"));
        Film film = Film.builder()
                .name("Original Title")
                .description("Original Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(new Rating(1L, "G"))
                .director(director)
                .build();

        Film createdFilm = filmStorage.create(film);

        createdFilm.setName("Updated Title");
        createdFilm.setDescription("Updated Description");
        createdFilm.setDuration(130);

        Film updatedFilm = filmStorage.update(createdFilm);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getId()).isEqualTo(createdFilm.getId());
        assertThat(updatedFilm.getName()).isEqualTo("Updated Title");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedFilm.getDuration()).isEqualTo(130);
        assertThat(updatedFilm.getReleaseDate()).isEqualTo(film.getReleaseDate()); // без изменений

        Optional<Film> filmOptional = Optional.of(filmStorage.findFilmById(updatedFilm.getId()));
        assertThat(filmOptional).isPresent();
        assertThat(filmOptional.get().getName()).isEqualTo("Updated Title");
        assertThat(filmOptional.get().getDescription()).isEqualTo("Updated Description");
    }
}

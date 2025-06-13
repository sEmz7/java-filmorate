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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class,
        DirectorsDbStorage.class, DirectorRowMapper.class,
        FilmDbStorage.class, FilmRowMapper.class,
        GenresDbStorage.class, GenreRowMapper.class,
        RatingDbStorage.class, RatingRowMapper.class,
        LikesDbStorage.class, LikeRowMapper.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private DirectorsDbStorage directorsDbStorage;

    @Autowired
    private LikesDbStorage likesDbStorage;

    @Autowired
    private UserDbStorage userDbStorage;

    @Test
    void testFindFilmById() {
        Film film = Film.builder()
                .name("test")
                .description("test")
                .releaseDate(LocalDate.of(2005, 1, 1))
                .duration(133)
                .mpa(new Rating(1L, "G"))
                .build();

        Film createdFilm = filmStorage.create(film);

        Optional<Film> foundFilm = Optional.of(filmStorage.findFilmById(createdFilm.getId()));

        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("id", createdFilm.getId()));
    }


    @Test
    void testCreateFilm() {
        Film newFilm = Film.builder()
                .name("Inception")
                .description("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpa(new Rating(1L, "G"))
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
        Film film = Film.builder()
                .name("Original Title")
                .description("Original Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(new Rating(1L, "G"))
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

    @Test
    void findFilmByDirectorSortByYear() {
        Director director1 = directorsDbStorage.create(new Director("dir1"));
        Film film1 = Film.builder()
                .name("Original Title")
                .description("Original Description")
                .releaseDate(LocalDate.of(2006, 1, 1))
                .duration(120)
                .mpa(new Rating(1L, "G"))
                .directors(List.of(director1))
                .build();
        Film film2 = Film.builder()
                .name("Original Title")
                .description("Original Description")
                .releaseDate(LocalDate.of(2002, 1, 1))
                .duration(120)
                .mpa(new Rating(1L, "G"))
                .directors(List.of(director1))
                .build();
        Film film3 = Film.builder()
                .name("Original Title")
                .description("Original Description")
                .releaseDate(LocalDate.of(2004, 1, 1))
                .duration(120)
                .mpa(new Rating(1L, "G"))
                .directors(List.of(director1))
                .build();

        Film cf1 = filmStorage.create(film1);
        Film cf2 = filmStorage.create(film2);
        Film cf3 = filmStorage.create(film3);

        List<Film> films = filmStorage.findFilmsByDirectorSortYear(director1.getId());
        assertEquals(films.getFirst().getId(), film2.getId());
        assertEquals(films.getLast().getId(), film1.getId());
        assertEquals(films.size(), 3);
    }

    @Test
    void findFilmByDirectorSortByLikes() {
        Director director1 = directorsDbStorage.create(new Director("dir1"));
        User user1 = User.builder()
                .email("test@example1.com")
                .login("testlogin1")
                .name("Test User1")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User user2 = User.builder()
                .email("test@example2.com")
                .login("testlogin2")
                .name("Test User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User user3 = User.builder()
                .email("test@example3.com")
                .login("testlogin3")
                .name("Test User3")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        Film film1 = Film.builder()
                .name("Original Title1")
                .description("Original Description1")
                .releaseDate(LocalDate.of(2006, 1, 1))
                .duration(120)
                .mpa(new Rating(1L, "G"))
                .directors(List.of(director1))
                .build();
        Film film2 = Film.builder()
                .name("Original Title2")
                .description("Original Description2")
                .releaseDate(LocalDate.of(2002, 1, 1))
                .duration(120)
                .mpa(new Rating(1L, "G"))
                .directors(List.of(director1))
                .build();
        Film film3 = Film.builder()
                .name("Original Title3")
                .description("Original Description3")
                .releaseDate(LocalDate.of(2004, 1, 1))
                .duration(120)
                .mpa(new Rating(1L, "G"))
                .directors(List.of(director1))
                .build();

        User u1 = userDbStorage.create(user1);
        User u2 = userDbStorage.create(user2);
        User u3 = userDbStorage.create(user3);
        Film cf1 = filmStorage.create(film1);
        Film cf2 = filmStorage.create(film2);
        Film cf3 = filmStorage.create(film3);
        likesDbStorage.addLike(cf2.getId(), u1.getId());
        likesDbStorage.addLike(cf2.getId(), u2.getId());
        likesDbStorage.addLike(cf2.getId(), u3.getId());
        likesDbStorage.addLike(cf3.getId(), u1.getId());

        List<Film> films = filmStorage.findFilmsByDirectorSortLikes(director1.getId());
        assertEquals(films.getFirst().getId(), cf2.getId());
        assertEquals(films.getLast().getId(), cf1.getId());
        assertEquals(films.size(), 3);
    }
}

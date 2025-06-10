package ru.yandex.practicum.filmorate.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.*;
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
        FilmDbStorage.class, FilmRowMapper.class,
        GenresDbStorage.class, GenreRowMapper.class,
        RatingDbStorage.class, RatingRowMapper.class,
        LikesDbStorage.class, LikeRowMapper.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private LikesDbStorage likesStorage;

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void testFindFilmById() {
        Film film = Film.builder()
                .name("test")
                .description("test")
                .releaseDate(LocalDate.of(2005, 1, 1))
                .duration(133)
                .director("director")
                .mpa(new Rating(1L, "G"))
                .build();

        Film createdFilm = filmStorage.create(film);

        Optional<Film> foundFilm = Optional.of(filmStorage.findFilmById(createdFilm.getId()));

        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("id", createdFilm.getId()));
    }

    @Test
    void searchTest() {
        Film film1 = Film.builder()
                .name("Крадущийся тигр, затаившийся дракон")
                .description("test")
                .releaseDate(LocalDate.of(2005, 1, 1))
                .duration(133)
                .director("Энг Ли")
                .mpa(new Rating(1L, "G"))
                .build();

        Film film2 = Film.builder()
                .name("Крадущийся в ночи")
                .description("test")
                .releaseDate(LocalDate.of(2005, 1, 1))
                .duration(133)
                .director("Фрэнк Спотниц")
                .mpa(new Rating(1L, "G"))
                .build();

        User user1 = User.builder()
                .email("test@example.com")
                .login("testlogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User user2 = User.builder()
                .email("test@example.com")
                .login("testlogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User user3 = User.builder()
                .email("test@example.com")
                .login("testlogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Film createdFilm1 = filmStorage.create(film1);
        Film createdFilm2 = filmStorage.create(film2);
        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);
        User createdUser3 = userStorage.create(user3);

        likesStorage.addLike(2, 1);
        likesStorage.addLike(2, 2);
        likesStorage.addLike(1, 3);

        List<Film> searchByDirectorFilm = filmStorage.search("спот", List.of("director"));
        List<Film> searchByNameFilm = filmStorage.search("крад", List.of("title"));
        List<Film> searchByDirectorAndNameFilm = filmStorage.search("крад", List.of("director", "title"));

        assertThat(searchByNameFilm.getFirst().getName()).isEqualTo(createdFilm2.getName());
        assertThat(searchByNameFilm.getLast().getName()).isEqualTo(createdFilm1.getName());
        assertThat(searchByDirectorFilm.getFirst().getDirector()).isEqualTo(createdFilm2.getDirector());
        assertEquals(searchByDirectorAndNameFilm.size(), 0);
    }


    @Test
    void testCreateFilm() {
        Film newFilm = Film.builder()
                .name("Inception")
                .description("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .director("director")
                .mpa(new Rating(1L, "G"))
                .build();

        Film createdFilm = filmStorage.create(newFilm);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo(newFilm.getName());
        assertThat(createdFilm.getDescription()).isEqualTo(newFilm.getDescription());
        assertThat(createdFilm.getReleaseDate()).isEqualTo(newFilm.getReleaseDate());
        assertThat(createdFilm.getDuration()).isEqualTo(newFilm.getDuration());
        assertThat(createdFilm.getDirector()).isEqualTo(newFilm.getDirector());

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
                .director("director")
                .mpa(new Rating(1L, "G"))
                .build();

        Film createdFilm = filmStorage.create(film);

        createdFilm.setName("Updated Title");
        createdFilm.setDescription("Updated Description");
        createdFilm.setDuration(130);
        createdFilm.setDirector("newDirector");

        Film updatedFilm = filmStorage.update(createdFilm);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getId()).isEqualTo(createdFilm.getId());
        assertThat(updatedFilm.getName()).isEqualTo("Updated Title");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedFilm.getDuration()).isEqualTo(130);
        assertThat(updatedFilm.getDirector()).isEqualTo("newDirector");
        assertThat(updatedFilm.getReleaseDate()).isEqualTo(film.getReleaseDate()); // без изменений

        Optional<Film> filmOptional = Optional.of(filmStorage.findFilmById(updatedFilm.getId()));
        assertThat(filmOptional).isPresent();
        assertThat(filmOptional.get().getName()).isEqualTo("Updated Title");
        assertThat(filmOptional.get().getDescription()).isEqualTo("Updated Description");
    }
}

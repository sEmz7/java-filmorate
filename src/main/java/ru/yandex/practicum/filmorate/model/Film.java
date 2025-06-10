package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Size(max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @Min(1)
    private int duration;

    private String director;

    private Set<Long> likes;

    private List<Genre> genres;

    private Rating mpa;
}

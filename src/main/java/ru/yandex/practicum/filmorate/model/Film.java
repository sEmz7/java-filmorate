package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

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
    private Duration duration;

    public long getDuration() {
        return duration.toMinutes();
    }

    public void setDuration(long minutes) {
        duration = Duration.ofMinutes(minutes);
    }
}

package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Director {
    private Long id;
    @NotBlank
    private String name;

    public Director(String name) {
        this.name = name;
    }
}

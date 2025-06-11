package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorsDbStorage;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorsDbStorage directorsStorage;

    @GetMapping
    public Collection<Director> findAll() {
        return directorsStorage.findAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable long id) {
        return directorsStorage.findById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        return directorsStorage.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorsStorage.update(director);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Director> delete(@PathVariable long id) {
        Director director = directorsStorage.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(director);
    }
}


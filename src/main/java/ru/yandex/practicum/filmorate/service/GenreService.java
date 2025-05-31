package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenresDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenresDbStorage genresDbStorage;

    public List<Genre> findAll() {
        return genresDbStorage.findAll();
    }

    public Genre findById(long id) {
        return genresDbStorage.getGenreById(id);
    }
}

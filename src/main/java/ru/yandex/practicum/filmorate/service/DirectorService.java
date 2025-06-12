package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorsDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorsDbStorage directorsDbStorage;

    public List<Director> findAll() {
        return directorsDbStorage.findAll();
    }

    public Director findById(long id) {
        return directorsDbStorage.findById(id);
    }

    public Director create(Director director) {
        return directorsDbStorage.create(director);
    }

    public Director update(Director director) {
        return directorsDbStorage.update(director);
    }

    public Director delete(long id) {
        return directorsDbStorage.delete(id);
    }
}

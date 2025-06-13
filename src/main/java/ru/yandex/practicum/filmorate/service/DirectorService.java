package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorsDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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
        if (directorsDbStorage.findById(director.getId()) == null) {
            log.warn("Директор с id {} не найден", director.getId());
            return directorsDbStorage.create(director);
        }
        return directorsDbStorage.update(director);
    }

    public Director delete(long id) {
        return directorsDbStorage.delete(id);
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.db.RatingDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final RatingDbStorage ratingDbStorage;

    public Rating getById(long id) {
        return ratingDbStorage.getRatingById(id);
    }

    public List<Rating> findAll() {
        return ratingDbStorage.findAll();
    }
}

package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper reviewRowMapper;
    private static final String FIND_ALL = "SELECT * FROM reviews";
    private static final String CREATE =
            "INSERT INTO reviews (content, isPositive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE reviews SET content = ?, isPositive = ?, user_id = ?, film_id = ? WHERE review_id = ?";
    private static final String DELETE = "DELETE FROM reviews WHERE review_id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM reviews WHERE review_id = ?";

    public Collection<Review> findAll() {
        return jdbc.query(FIND_ALL, reviewRowMapper);
    }

    public Review create(Review review) {
        Long id = insert(CREATE,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                0);
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    public Review update(Review review) {
        jdbc.update(UPDATE,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId());
        return review;
    }

    public void delete(long id) {
        jdbc.update(DELETE, id);
    }

    public Optional<Review> findById(long id) {
        return jdbc.query(FIND_BY_ID, reviewRowMapper, id).stream().findFirst();
    }

    private long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            return id;
        }
        throw new InternalServerException("Не удалось сохранить данные");
    }
}

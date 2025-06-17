package ru.yandex.practicum.filmorate.storage.db;

import java.util.Collection;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.mapper.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

@Repository
public class EventDbStorage extends BaseDbStorage {
    private final EventRowMapper eventRowMapper;

    private static final String SAVE_EVENT = """
            INSERT INTO events (type, operation, entity_id, user_id, created_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)""";
    private static final String GET_EVENTS_BY_USER_ID = "SELECT * FROM events WHERE user_id = ? ORDER BY created_at DESC";

    public EventDbStorage(JdbcTemplate jdbc, EventRowMapper eventRowMapper) {
        super(jdbc);
        this.eventRowMapper = eventRowMapper;
    }

    public void saveEvent(Event event) {
        insert(SAVE_EVENT, event.getType().name(), event.getOperation().name(), event.getEntityId(), event.getUserId());
    }

    public Collection<Event> getEventsByUserId(long userId) {
        return jdbc.query(GET_EVENTS_BY_USER_ID, eventRowMapper, userId);
    }

}

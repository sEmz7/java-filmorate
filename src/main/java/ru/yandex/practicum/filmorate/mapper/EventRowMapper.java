package ru.yandex.practicum.filmorate.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Event;

@Component
public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(rs.getLong("id"))
                .type(Event.Type.valueOf(rs.getString("type")))
                .operation(Event.Operation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .userId(rs.getLong("user_id"))
                .createdAt(rs.getTimestamp("created_at").getTime())
                .build();
    }

}

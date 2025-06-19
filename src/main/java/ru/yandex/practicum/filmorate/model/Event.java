package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private Long eventId;
    private Type eventType;
    private Operation operation;
    private Long entityId;
    private Long userId;
    private Long timestamp;

    public enum Type {
        LIKE, REVIEW, FRIEND
    }

    public enum Operation {
        REMOVE, ADD, UPDATE
    }

}

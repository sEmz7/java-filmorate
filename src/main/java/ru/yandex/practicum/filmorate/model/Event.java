package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private Long id;
    private Type type;
    private Operation operation;
    private Long entityId;
    private Long userId;
    private Long createdAt;

    public enum Type {
        LIKE, REVIEW, FRIEND
    }

    public enum Operation {
        REMOVE, ADD, UPDATE
    }

}

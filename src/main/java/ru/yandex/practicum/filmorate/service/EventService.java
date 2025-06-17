package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.db.EventDbStorage;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventDbStorage eventDbStorage;

    public void saveEvent(Event.Type type, Event.Operation operation, long entityId, long userId) {
        eventDbStorage.saveEvent(Event.builder()
                        .eventType(type)
                        .operation(operation)
                        .entityId(entityId)
                        .userId(userId)
                .build());
    }

    public Collection<Event> getFeedByUserId(long userId) {
        return eventDbStorage.getEventsByUserId(userId);
    }

}

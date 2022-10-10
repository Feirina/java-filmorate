package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.Mappers;

import java.time.Instant;
import java.util.List;

@Component
public class EventDbStorage implements EventDaoStorage {
    private final JdbcTemplate jdbcTemplate;

    private final Mappers mappers;

    public EventDbStorage(JdbcTemplate jdbcTemplate, Mappers mappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.mappers = mappers;
    }

    @Override
    public void fixEvent(Long userId, Long entityId, EventType eventType, Operation operation) {
        long timestamp = Instant.now().toEpochMilli();
        String sql = "INSERT INTO FEED (timestamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, timestamp, userId, eventType.name(), operation.name(), entityId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Event> getFeed(Long userId) {
        String sql = "SELECT * FROM FEED  WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeEvent(rs), userId);
    }
}

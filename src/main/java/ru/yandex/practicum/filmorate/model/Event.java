package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"eventId", "timestamp"})
public class Event {
    private Long eventId;

    private Long entityId;

    private Long userId;

    private EventType eventType;

    private Operation operation;

    private Long timestamp;
}

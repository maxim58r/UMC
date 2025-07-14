package ru.mts.media.platform.umc.domain.event;


import ru.mts.media.platform.umc.domain.gql.types.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventSot {
    List<Event> getAllEvents();

    Optional<Event> getEventById(UUID id);

    List<Event> findEventsByVenueReferenceId(String id, Integer limit);

    Optional<Event> save(Event event);
}

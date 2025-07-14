package ru.mts.media.platform.umc.domain.event;

import ru.mts.media.platform.umc.domain.gql.types.CreateEventInput;
import ru.mts.media.platform.umc.domain.gql.types.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventDomainService {
    Optional<EventSave> create(CreateEventInput input);

    List<Event> getEvents();

    Event getEventById(UUID id);
}

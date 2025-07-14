package ru.mts.media.platform.umc.api.gql.event;

import com.netflix.graphql.dgs.*;
import lombok.RequiredArgsConstructor;
import ru.mts.media.platform.umc.domain.event.EventDomainService;
import ru.mts.media.platform.umc.domain.gql.types.Event;

import java.util.List;
import java.util.UUID;

@DgsComponent
@RequiredArgsConstructor
public class EventDgsQuery {

    private final EventDomainService eventDomainService;

    @DgsQuery
    public List<Event> events() {
        return eventDomainService.getEvents();
    }

    @DgsQuery
    public Event eventById(@InputArgument("id") String id) {
        return eventDomainService.getEventById(UUID.fromString(id));
    }
}

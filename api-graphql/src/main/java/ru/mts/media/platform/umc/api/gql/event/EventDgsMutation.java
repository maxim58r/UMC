package ru.mts.media.platform.umc.api.gql.event;

import com.netflix.graphql.dgs.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.mts.media.platform.umc.domain.common.EntityEvent;
import ru.mts.media.platform.umc.domain.event.EventDomainService;
import ru.mts.media.platform.umc.domain.gql.types.CreateEventInput;
import ru.mts.media.platform.umc.domain.gql.types.Event;

import java.util.Optional;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class EventDgsMutation {

    private final EventDomainService eventDomainService;


    @DgsMutation
    public Optional<Event> createEvent(@InputArgument("input") CreateEventInput input) {
        return eventDomainService.create(input)
                .map(EntityEvent::getEntity);
    }
}

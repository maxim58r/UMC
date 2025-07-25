package ru.mts.media.platform.umc.domain.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.mts.media.platform.umc.domain.gql.types.CreateEventInput;
import ru.mts.media.platform.umc.domain.gql.types.Event;
import ru.mts.media.platform.umc.domain.gql.types.Venue;
import ru.mts.media.platform.umc.domain.venue.VenueSot;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventDomainServiceImpl implements EventDomainService {
    private final ApplicationEventPublisher eventPublisher;
    private final EventSot eventSot;
    private final EventDomainServiceMapper eventDomainServiceMapper;
    private final VenueSot venueSot;

    @Transactional
    @Override
    public Optional<EventSave> create(CreateEventInput input) {
        List<Venue> list = Optional.ofNullable(input)
                .map(CreateEventInput::getVenueReferenceIds)
                .stream()
                .flatMap(Collection::stream)
                .map(venueSot::getVenueByReferenceId)
                .flatMap(Optional::stream)
                .toList();


        Event event = eventDomainServiceMapper.create(input);
        event.setVenues(list);

        Optional<Event> savedEventOpt = eventSot.save(event);

        if (savedEventOpt.isEmpty()) {
            log.warn("Event was not saved: {}", event);
            return Optional.empty();
        }

        Event savedEvent = savedEventOpt.get();

        if (savedEvent.getVenues() == null) {
            savedEvent.setVenues(List.of());
        }

        EventSave wrapper = new EventSave(savedEvent);
        eventPublisher.publishEvent(wrapper);

        log.info("Resulting event: {}", wrapper);
        return Optional.of(wrapper);
    }


    @Override
    public List<Event> getEvents() {
        return eventSot.getAllEvents();
    }

    @Override
    public Event getEventById(UUID id) {
        return eventSot.getEventById(id).orElse(null);
    }
}



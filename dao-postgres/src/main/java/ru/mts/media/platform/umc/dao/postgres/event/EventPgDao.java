package ru.mts.media.platform.umc.dao.postgres.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.mts.media.platform.umc.dao.postgres.venue.VenuePgMapper;
import ru.mts.media.platform.umc.domain.event.EventSot;
import ru.mts.media.platform.umc.domain.gql.types.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventPgDao implements EventSot {
    private final EventPgRepository eventRepository;
    private final EventPgMapper eventPgMapper;
    private final VenuePgMapper venuePgMapper;

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(it -> eventPgMapper.asModel(it, venuePgMapper))
                .toList();
    }


    @Override
    public Optional<Event> getEventById(UUID id) {
        return eventRepository.findById(id)
                .map(it -> eventPgMapper.asModel(it, venuePgMapper));
    }

    @Override
    public List<Event> findEventsByVenueReferenceId(String id, Integer limit) {
        if (id == null) {
            throw new IllegalArgumentException("referenceId must not be null");
        }

        if (limit <= 0) limit = 5;

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "startTime"));

        return eventRepository.findByVenues_ReferenceId(id, pageable)
                .stream()
                .map(it -> eventPgMapper.asModel(it, venuePgMapper))
                .toList();
    }

    @Override
    public Optional<Event> save(Event event) {
        return Optional.ofNullable(event)
                .map(eventPgMapper::asEntity)
                .map(eventRepository::save)
                .map(it -> eventPgMapper.asModel(it, venuePgMapper));
    }
}

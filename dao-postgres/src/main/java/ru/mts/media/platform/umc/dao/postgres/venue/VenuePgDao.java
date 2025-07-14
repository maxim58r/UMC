package ru.mts.media.platform.umc.dao.postgres.venue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.mts.media.platform.umc.domain.gql.types.FullExternalId;
import ru.mts.media.platform.umc.domain.gql.types.Venue;
import ru.mts.media.platform.umc.domain.venue.VenueSave;
import ru.mts.media.platform.umc.domain.venue.VenueSot;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
class VenuePgDao implements VenueSot {
    private final VenuePgRepository repository;
    private final VenuePgMapper mapper;

    @Override
    public Optional<Venue> getVenueByReferenceId(String id) {
        return Optional.ofNullable(id)
                .map(repository::findByReferenceId)
                .map(mapper::asModel);
    }

    @Override
    public Optional<Venue> getVenueByFullExternalId(FullExternalId externalId) {
        return Optional.ofNullable(externalId)
                .map(mapper::asPk)
                .flatMap(repository::findById)
                .map(mapper::asModel);
    }

    @Override
    public Optional<Venue> save(Venue venue) {
        return Optional.ofNullable(venue)
                .map(mapper::asEntity)
                .map(repository::save)
                .map(mapper::asModel);
    }

    @EventListener
    public void handleVenueCreatedEvent(VenueSave evt) {
        log.info("Venue before created: {}", evt);
        evt.unwrap()
                .map(mapper::asEntity)
                .ifPresent(repository::save);
        log.info("Venue after created: {}", evt);
    }
}

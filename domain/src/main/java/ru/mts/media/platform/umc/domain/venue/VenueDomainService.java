package ru.mts.media.platform.umc.domain.venue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.mts.media.platform.umc.domain.gql.types.FullExternalId;
import ru.mts.media.platform.umc.domain.gql.types.SaveVenueInput;
import ru.mts.media.platform.umc.domain.gql.types.Venue;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VenueDomainService {
    private final ApplicationEventPublisher eventPublisher;
    private final VenueSot sot;
    private final VenueDomainServiceMapper mapper;

    public Optional<VenueSave> save(FullExternalId id, SaveVenueInput input) {
        log.info("Before save venue: {}", input);

        Venue venue = sot.getVenueByFullExternalId(id)
                .map(existing -> mapper.patch(existing, input))
                .orElseGet(() -> mapper.create(id, input));

        return sot.save(venue)
                .map(VenueSave::new)
                .map(it -> {
                    log.info("Resulting venue: {}", it);
                    eventPublisher.publishEvent(it);
                    return it;
                });
    }
}

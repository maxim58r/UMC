package ru.mts.media.platform.umc.api.gql.venue;

import com.netflix.graphql.dgs.*;
import lombok.RequiredArgsConstructor;
import ru.mts.media.platform.umc.domain.event.EventSot;
import ru.mts.media.platform.umc.domain.gql.types.Event;
import ru.mts.media.platform.umc.domain.gql.types.Venue;
import ru.mts.media.platform.umc.domain.venue.VenueSot;

import java.util.List;

@DgsComponent
@RequiredArgsConstructor
public class VenueDgsQuery {
    private final VenueSot venueSot;
    private final EventSot eventSot;

    @DgsQuery
    public Venue venueByReferenceId(@InputArgument("id") String id) {
        return venueSot.getVenueByReferenceId(id).orElse(null);
    }

    @DgsData(parentType = "Venue", field = "recentEvents")
    public List<Event> recentEvents(DgsDataFetchingEnvironment dfe,
                                    @InputArgument("limit") Integer limit) {
        Venue venue = dfe.getSource();
        if (venue == null || venue.getId() == null) return List.of();
        if (limit == null || limit <= 0) limit = 5;
        return eventSot.findEventsByVenueReferenceId(venue.getId(), limit);
    }
}


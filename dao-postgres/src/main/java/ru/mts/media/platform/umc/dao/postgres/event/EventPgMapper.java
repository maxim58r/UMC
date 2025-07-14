package ru.mts.media.platform.umc.dao.postgres.event;

import org.mapstruct.*;
import ru.mts.media.platform.umc.dao.postgres.venue.VenuePgEntity;
import ru.mts.media.platform.umc.dao.postgres.venue.VenuePgMapper;
import ru.mts.media.platform.umc.domain.gql.types.CreateEventInput;
import ru.mts.media.platform.umc.domain.gql.types.Event;
import ru.mts.media.platform.umc.domain.gql.types.Venue;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(
        componentModel = SPRING,
        uses = {DateTimeMapperEventPg.class,
                VenuePgMapper.class}
)
public interface EventPgMapper {

    @Mapping(target = "startTime", source = "startTime", qualifiedByName = "asString")
    @Mapping(target = "endTime", source = "endTime", qualifiedByName = "asString")
    @Mapping(target = "venues", expression = "java(mapVenues(eventPg.getVenues(), venuePgMapper))")
    Event asModel(EventPgEntity eventPg, @Context VenuePgMapper venuePgMapper);

    @Mapping(target = "venues", ignore = true)
    @Mapping(target = "startTime", source = "startTime", qualifiedByName = "asLocalDateTime")
    @Mapping(target = "endTime", source = "endTime", qualifiedByName = "asLocalDateTime")
    EventPgEntity asEntity(CreateEventInput input);

//    @Mapping(target = "venues", source = ,ignore = true)
    @Mapping(target = "startTime", source = "startTime", qualifiedByName = "asLocalDateTime")
    @Mapping(target = "endTime", source = "endTime", qualifiedByName = "asLocalDateTime")
    EventPgEntity asEntity(Event input);

    default List<Venue> mapVenues(Set<VenuePgEntity> eventPg, @Context VenuePgMapper venuePgMapper) {
        if (eventPg == null) return Collections.emptyList();
        return eventPg.stream()
                .map(venuePgMapper::asModel)
                .toList();
    }
}

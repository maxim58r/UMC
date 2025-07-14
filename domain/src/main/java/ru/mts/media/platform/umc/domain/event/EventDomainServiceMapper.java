package ru.mts.media.platform.umc.domain.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mts.media.platform.umc.domain.gql.types.CreateEventInput;
import ru.mts.media.platform.umc.domain.gql.types.Event;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING,
        uses = {DateTimeMapperEvent.class},
        imports = {java.util.ArrayList.class})
public interface EventDomainServiceMapper {

    @Mapping(target = "startTime", source = "startTime", qualifiedByName = "asString")
    @Mapping(target = "endTime", source = "endTime", qualifiedByName = "asString")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venues", expression = "java(new java.util.ArrayList<>())")
    Event create(CreateEventInput input);
}

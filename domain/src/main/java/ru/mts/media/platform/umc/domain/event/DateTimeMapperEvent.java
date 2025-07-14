package ru.mts.media.platform.umc.domain.event;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeMapperEvent {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Named("asLocalDateTime")
    public LocalDateTime asLocalDateTime(String value) {
        if (value == null) return null;
        return LocalDateTime.parse(value, FORMATTER);
    }

    @Named("asString")
    public String asString(LocalDateTime value) {
        if (value == null) return null;
        return value.format(FORMATTER);
    }
}

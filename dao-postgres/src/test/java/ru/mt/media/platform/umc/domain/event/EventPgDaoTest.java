package ru.mt.media.platform.umc.domain.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;
import ru.mts.media.platform.umc.dao.postgres.event.EventPgDao;
import ru.mts.media.platform.umc.dao.postgres.event.EventPgEntity;
import ru.mts.media.platform.umc.dao.postgres.event.EventPgRepository;
import ru.mts.media.platform.umc.dao.postgres.event.EventPgMapper;
import ru.mts.media.platform.umc.dao.postgres.venue.VenuePgMapper;
import ru.mts.media.platform.umc.domain.gql.types.Event;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventPgDaoTest {

    private EventPgRepository eventRepository;
    private EventPgMapper eventPgMapper;
    private VenuePgMapper venuePgMapper;
    private EventPgDao eventPgDao;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventPgRepository.class);
        eventPgMapper = mock(EventPgMapper.class);
        venuePgMapper = mock(VenuePgMapper.class);
        eventPgDao = new EventPgDao(eventRepository, eventPgMapper, venuePgMapper);
    }

    @Test
    @DisplayName("Возвращает события по referenceId площадки с лимитом")
    void shouldReturnEventsByVenueReferenceIdWithLimit() {
        String refId = "venue-ref-1";
        int limit = 2;

        EventPgEntity entity1 = new EventPgEntity();
        entity1.setId(UUID.randomUUID());
        EventPgEntity entity2 = new EventPgEntity();
        entity2.setId(UUID.randomUUID());

        when(eventRepository.findByVenues_ReferenceId(eq(refId), any(Pageable.class)))
                .thenReturn(List.of(entity1, entity2));

        Event event1 = new Event();
        event1.setId(entity1.getId().toString());
        Event event2 = new Event();
        event2.setId(entity2.getId().toString());

        when(eventPgMapper.asModel(entity1, venuePgMapper)).thenReturn(event1);
        when(eventPgMapper.asModel(entity2, venuePgMapper)).thenReturn(event2);

        List<Event> result = eventPgDao.findEventsByVenueReferenceId(refId, limit);

        assertAll(
                () -> assertNotNull(result, "Результат не должен быть null"),
                () -> assertEquals(2, result.size(), "Должно быть ровно 2 события"),
                () -> assertEquals(event1, result.get(0), "Первое событие должно совпадать"),
                () -> assertEquals(event2, result.get(1), "Второе событие должно совпадать")
        );

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(eventRepository).findByVenues_ReferenceId(eq(refId), captor.capture());
        assertEquals(limit, captor.getValue().getPageSize());
    }

    @Test
    @DisplayName("Возвращает пустой список, если событий по площадке нет")
    void shouldReturnEmptyList_whenNoEvents() {
        String refId = "not-exist";
        int limit = 5;

        when(eventRepository.findByVenues_ReferenceId(eq(refId), any(Pageable.class)))
                .thenReturn(List.of());

        List<Event> result = eventPgDao.findEventsByVenueReferenceId(refId, limit);

        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Список событий должен быть пустым");
    }

    @Test
    @DisplayName("При лимите 0 возвращается пустой список")
    void shouldReturnEmptyList_whenLimitIsZero() {
        String refId = "venue-ref-1";
        int limit = 0;

        when(eventRepository.findByVenues_ReferenceId(eq(refId), any(Pageable.class)))
                .thenReturn(List.of());

        List<Event> result = eventPgDao.findEventsByVenueReferenceId(refId, limit);

        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Список событий должен быть пустым, если limit=0");
    }

    @Test
    @DisplayName("Если referenceId null, выбрасывается IllegalArgumentException")
    void shouldThrowException_whenReferenceIdIsNull() {
        int limit = 3;
        assertThrows(IllegalArgumentException.class, () -> eventPgDao.findEventsByVenueReferenceId(null, limit));
    }
}

package ru.mts.media.platform.umc.domain.event;

import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationEventPublisher;
import ru.mts.media.platform.umc.domain.common.EntityEvent;
import ru.mts.media.platform.umc.domain.gql.types.CreateEventInput;
import ru.mts.media.platform.umc.domain.gql.types.Event;
import ru.mts.media.platform.umc.domain.venue.VenueSot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventDomainServiceImplTest {

    private static final String TEST_EVENT_NAME = "TestEvent";
    private static final String TEST_START = "2024-08-01T12:00:00";
    private static final String TEST_END = "2024-08-01T18:00:00";
    private static final String TEST_VENUE = "venue-1";

    private ApplicationEventPublisher publisher;
    private EventSot eventSot;
    private EventDomainServiceMapper mapper;
    private EventDomainServiceImpl service;

    @BeforeEach
    void setUp() {
        publisher = mock(ApplicationEventPublisher.class);
        eventSot = mock(EventSot.class);
        mapper = mock(EventDomainServiceMapper.class);
        VenueSot venueSot = mock(VenueSot.class);
        service = new EventDomainServiceImpl(publisher, eventSot, mapper, venueSot);
    }

    @Test
    void shouldPublishEvent_whenCreateEventCalled() {
        CreateEventInput input = new CreateEventInput();
        input.setName(TEST_EVENT_NAME);
        input.setStartTime(TEST_START);
        input.setEndTime(TEST_END);
        input.setVenueReferenceIds(List.of(TEST_VENUE));

        Event event = new Event();
        event.setId(UUID.randomUUID().toString());
        event.setName(TEST_EVENT_NAME);
        event.setStartTime(TEST_START);
        event.setEndTime(TEST_END);

        when(mapper.create(input)).thenReturn(event);
        when(eventSot.save(event)).thenReturn(Optional.of(event));

        Optional<EventSave> result = service.create(input);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(event, result.map(EntityEvent::getEntity).orElse(null))
        );
        verify(publisher, times(1)).publishEvent(any(EventSave.class));
    }

    @Test
    void shouldReturnAllEvents_whenGetEventsCalled() {
        Event event1 = new Event();
        event1.setId("1");
        Event event2 = new Event();
        event2.setId("2");

        when(eventSot.getAllEvents()).thenReturn(List.of(event1, event2));

        List<Event> events = service.getEvents();

        assertAll(
                () -> assertNotNull(events),
                () -> assertEquals(2, events.size()),
                () -> assertEquals("1", events.getFirst().getId())
        );
    }

    @Test
    void shouldReturnEventById_whenEventExists() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.setId(eventId.toString());

        when(eventSot.getEventById(eventId)).thenReturn(Optional.of(event));

        Event found = service.getEventById(eventId);

        assertAll(
                () -> assertNotNull(found),
                () -> assertEquals(eventId.toString(), found.getId())
        );
    }

    @Test
    void shouldReturnNull_whenEventNotFoundById() {
        UUID notExistId = UUID.randomUUID();

        when(eventSot.getEventById(notExistId)).thenReturn(Optional.empty());

        Event found = service.getEventById(notExistId);

        assertNull(found, "If event not found, must return null");
    }

    @Test
    void shouldThrowException_whenInvalidInput() {
        CreateEventInput input = new CreateEventInput();
        input.setName(""); // Невалидное имя
        input.setStartTime(TEST_START);
        input.setEndTime(TEST_END);
        input.setVenueReferenceIds(List.of(TEST_VENUE));

        when(mapper.create(input)).thenThrow(new IllegalArgumentException("Event name must not be empty"));

        assertThrows(IllegalArgumentException.class, () -> service.create(input));
    }


    @Test
//    @Disabled("Нагрузочный тест — не запускается автоматически")
    @DisplayName("Should handle bulk event creation without error")
    @Timeout(10)
    void shouldHandleBulkEventCreationWithoutError() {
        int n = 1000;

        for (int i = 0; i < n; i++) {
            CreateEventInput input = new CreateEventInput();
            input.setName("PerfTestEvent-" + i);
            input.setStartTime(TEST_START);
            input.setEndTime(TEST_END);
            input.setVenueReferenceIds(List.of("venue-" + i));

            Event event = new Event();
            event.setId(UUID.randomUUID().toString());
            event.setName("PerfTestEvent-" + i);
            event.setStartTime(TEST_START);
            event.setEndTime(TEST_END);

            when(mapper.create(input)).thenReturn(event);
            when(eventSot.save(event)).thenReturn(Optional.of(event));

            var result = service.create(input);
            assertAll(
                    () -> assertTrue(result.isPresent(), "EventSave должен быть возвращён"),
                    () -> assertEquals(event, result.get().getEntity(), "Entity должен совпадать с замоканным Event")
            );
        }
    }
}

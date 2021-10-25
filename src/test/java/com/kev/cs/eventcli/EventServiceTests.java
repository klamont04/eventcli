package com.kev.cs.eventcli;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.kev.cs.eventcli.models.Event;
import com.kev.cs.eventcli.repositories.EventRepository;
import com.kev.cs.eventcli.services.EventService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventServiceTests {

    private Event eventMock;
    private List<Event> eventListMock;
    private EventRepository eventRepository;
    private EntityManager entityManager;

    @BeforeEach
    void initMocks(){
        eventMock = mock(Event.class);
        eventListMock = new ArrayList<>();
        eventListMock.add(eventMock);
        eventListMock.add(eventMock);
        eventRepository = mock(EventRepository.class);
        when(eventRepository.findAll()).thenReturn(eventListMock);
        entityManager = mock(EntityManager.class);
    }
    
    @Test
    void testList() {       
        EventService eventService = new EventService(eventRepository, entityManager);
        eventService.list();
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testSaveAll() {       
        EventService eventService = new EventService(eventRepository, entityManager);
        eventService.saveAll(eventListMock);
        verify(eventRepository, times(1)).saveAll(eventListMock);
    }

    @Test
    void testSave() {       
        EventService eventService = new EventService(eventRepository, entityManager);
        eventService.save(eventMock);
        verify(eventRepository, times(1)).save(eventMock);
    }

    @Test
    void testBatchSaveAll() {       
        EventService eventService = new EventService(eventRepository, entityManager);
        eventService.setBatchSize(1);
        eventService.batchSaveAll(eventListMock);
        verify(entityManager, times(1)).flush();
        verify(entityManager, times(1)).clear();
        verify(entityManager, times(2)).persist(eventListMock.get(0));
    }

}

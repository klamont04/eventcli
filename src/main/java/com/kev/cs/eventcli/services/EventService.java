package com.kev.cs.eventcli.services;

import java.util.List;

import javax.persistence.EntityManager;

import com.kev.cs.eventcli.models.Event;
import com.kev.cs.eventcli.repositories.EventRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {
    private EventRepository eventRepository;

    private EntityManager entityManager;

    @Value("${eventservice.entitymanager.batch.size}")
	private int batchSize;

    public EventService(EventRepository eventRepository, EntityManager entityManager) {
        this.eventRepository = eventRepository;
        this.entityManager = entityManager;
    }

    public Iterable<Event> list() {
        return eventRepository.findAll();
    }

    public Iterable<Event> saveAll(List<Event> events) {
        return eventRepository.saveAll(events);
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public boolean batchSaveAll(List<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
            entityManager.persist(events.get(i));
        }
        return true;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }


}

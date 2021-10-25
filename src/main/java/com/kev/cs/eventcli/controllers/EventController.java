package com.kev.cs.eventcli.controllers;

import com.kev.cs.eventcli.models.Event;
import com.kev.cs.eventcli.services.EventService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {
    private EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/list")
    public Iterable<Event> list() {
        return eventService.list();
    }
    
}

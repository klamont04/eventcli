package com.kev.cs.eventcli.repositories;

import com.kev.cs.eventcli.models.Event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,String> {
    
}

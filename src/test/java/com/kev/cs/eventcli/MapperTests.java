package com.kev.cs.eventcli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.kev.cs.eventcli.mapper.LogEntryToEvent;
import com.kev.cs.eventcli.models.Event;
import com.kev.cs.eventcli.models.LogEntry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MapperTests {
    public static final String HOST="host";
    public static final String ID = "id1";
    public static final String STATE = "FINISHED";
    public static final String TYPE = "APPLICATION_LOG";
    public static final Long ALERT_TRUE_TIME = 6L;
    public static final Long ALERT_FALSE_TIME = 3L;
    
    @Autowired
    LogEntryToEvent eventMapper;

    @Test
    void testLogEntryToEvent() {
        LogEntry logEntry = new LogEntry();
        logEntry.setHost(HOST);
        logEntry.setId(ID);
        logEntry.setState(STATE);
        logEntry.setTimestamp(ALERT_FALSE_TIME);
        logEntry.setType(TYPE);
        Event event = eventMapper.map(logEntry);
        assertEquals(STATE, logEntry.getState());
        assertEquals(HOST, event.getHost());
        assertEquals(ID, event.getEventId());
        assertEquals(ALERT_FALSE_TIME, event.getDuration());
        assertEquals(false, event.isAlert());
        assertEquals(TYPE, event.getType());
    } 

    @Test
    void testEventAlert() {
        LogEntry logEntry = new LogEntry();
        logEntry.setTimestamp(ALERT_FALSE_TIME);
        Event event = eventMapper.map(logEntry);
        assertEquals(false, event.isAlert());
        
        logEntry.setTimestamp(ALERT_TRUE_TIME);
        event = eventMapper.map(logEntry);
        assertEquals(true, event.isAlert());
    }
}

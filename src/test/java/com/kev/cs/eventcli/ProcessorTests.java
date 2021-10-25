package com.kev.cs.eventcli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kev.cs.eventcli.mapper.LogEntryToEvent;
import com.kev.cs.eventcli.models.Event;
import com.kev.cs.eventcli.models.LogEntry;
import com.kev.cs.eventcli.processor.LogEntryEventProcessor;
import com.kev.cs.eventcli.services.EventService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;


class ProcessorTests {

    private Event eventMock;
    private EventService eventService;
    private LogEntryToEvent entryToEvent;

    @BeforeEach
    void before() {
        eventMock = mock(Event.class);
        eventService = mock(EventService.class);
        when(eventService.save(any(Event.class))).thenReturn(eventMock);
        entryToEvent = mock(LogEntryToEvent.class); 
    }

    @Test
    void checkProcessGeneratesDbInserts() { 
        LogEntryEventProcessor logEntryEventProcessor = new LogEntryEventProcessor(entryToEvent, eventService);
        logEntryEventProcessor.setBatchEventListSize(10);
        logEntryEventProcessor.process("src\\test\\resources\\logfile.txt");
        verify(eventService, times(1)).batchSaveAll(anyList());
    }

    @Test 
    void checkEventDurationCalculationStartBeforeFinish() {
        LogEntryEventProcessor logEntryEventProcessor = new LogEntryEventProcessor(entryToEvent, eventService);
        Map<String,LogEntry> processedEventsMap = new HashMap<>();
        LogEntry logEntryStart = new LogEntry();
        LogEntry logEntryFinish = new LogEntry();
        logEntryStart.setTimestamp(556019485L);
        logEntryFinish.setTimestamp(556019488L);
        String id = "Test1";
        logEntryFinish.setId(id);
        processedEventsMap.put(id,logEntryStart);
        logEntryEventProcessor.calculateEventDuration(processedEventsMap, logEntryFinish);
        assertEquals(3L, logEntryFinish.getTimestamp());
    }

    @Test 
    void checkEventDurationCalculationFinishBeforeStart() {
        LogEntryEventProcessor logEntryEventProcessor = new LogEntryEventProcessor(entryToEvent, eventService);
        Map<String,LogEntry> processedEventsMap = new HashMap<>();
        LogEntry logEntryStart = new LogEntry();
        LogEntry logEntryFinish = new LogEntry();
        logEntryStart.setTimestamp(556019485L);
        logEntryFinish.setTimestamp(556019488L);
        String id = "Test1";
        logEntryStart.setId(id);
        processedEventsMap.put(id,logEntryFinish);
        logEntryEventProcessor.calculateEventDuration(processedEventsMap, logEntryStart);
        assertEquals(3L, logEntryStart.getTimestamp());
    }

    @Test
    void checkProcessedEventRemovedFromMap() throws JsonProcessingException{ 
        LogEntryEventProcessor logEntryEventProcessor = new LogEntryEventProcessor(entryToEvent, eventService);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,LogEntry> processedEventsMap = new HashMap<>();
        LogEntry logEntryStart = new LogEntry();
        logEntryStart.setTimestamp(556019485L);
        String id = "Test1";
        logEntryStart.setId(id);
        processedEventsMap.put(id,logEntryStart);
        String jsonEntry = "{\"id\": \"Test1\", \"state\": \"FINISHED\", \"timestamp\": 556019487}";
        logEntryEventProcessor.processJson(processedEventsMap, objectMapper, jsonEntry);;
        assertEquals(0, processedEventsMap.size());
    }

    @Test
    void checkInvalidJsonEntryThrowsException() throws JsonProcessingException{
        LogEntryEventProcessor logEntryEventProcessor = new LogEntryEventProcessor(entryToEvent, eventService);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,LogEntry> processedEventsMap = new HashMap<>();
        LogEntry logEntryStart = new LogEntry();
        logEntryStart.setTimestamp(556019485L);
        String id = "Test1";
        logEntryStart.setId(id);
        processedEventsMap.put(id,logEntryStart);
        String jsonEntry = "{id\": \"Test1\", \"state\": \"FINISHED\", \"timestamp\": 556019487}";
        assertThrows(JsonParseException.class, () -> {
		    logEntryEventProcessor.processJson(processedEventsMap, objectMapper, jsonEntry);
    	});
    }

    @Test
    void problemProcessingFileCheck() {
        LogEntryEventProcessor logEntryEventProcessor = new LogEntryEventProcessor(entryToEvent, eventService);
        Logger logger = (Logger) LoggerFactory.getLogger(LogEntryEventProcessor.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
		logEntryEventProcessor.process("ImaginaryProblemFile.txt");
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("Failed to process file ImaginaryProblemFile.txt", logsList.get(0).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());

        assertEquals("IOException", logsList.get(1).getFormattedMessage());
        assertEquals(Level.ERROR, logsList.get(1).getLevel());
    }
}

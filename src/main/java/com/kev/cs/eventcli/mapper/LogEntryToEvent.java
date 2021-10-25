package com.kev.cs.eventcli.mapper;

import com.kev.cs.eventcli.models.Entry;
import com.kev.cs.eventcli.models.Event;
import com.kev.cs.eventcli.models.LogEntry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogEntryToEvent implements EntryToEventMapper {

	@Value("${alert.time}")
	private Long alertTime;

    @Override
    public Event map(Entry entry) {
        LogEntry logEntry = (LogEntry)entry;
        Event event = new Event();
		event.setEventId(logEntry.getId());
		event.setHost(logEntry.getHost());
		event.setType(logEntry.getType());
		event.setDuration(logEntry.getTimestamp());
		event.setAlert( logEntry.getTimestamp() > alertTime);
		return event;
    }
    
}

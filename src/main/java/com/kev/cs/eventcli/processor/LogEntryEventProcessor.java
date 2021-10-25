package com.kev.cs.eventcli.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kev.cs.eventcli.mapper.LogEntryToEvent;
import com.kev.cs.eventcli.models.Entry;
import com.kev.cs.eventcli.models.Event;
import com.kev.cs.eventcli.models.LogEntry;
import com.kev.cs.eventcli.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogEntryEventProcessor implements EventProcessor{
    public static final Logger logger = LoggerFactory.getLogger(LogEntryEventProcessor.class);

	@Value("${event.list.size}")
	private int batchEventListSize;
	
	private LogEntryToEvent entryToEvent;
	private EventService eventService;
	private List<Event> writeToDbList;

	public LogEntryEventProcessor(LogEntryToEvent entryToEvent, EventService eventService) {
		this.entryToEvent = entryToEvent;
		this.eventService = eventService;
		this.writeToDbList = new ArrayList<>();
	}

    @Override
    public void process(String file) {
        Map<String,LogEntry> processedEventsMap = new HashMap<>(); 
		ObjectMapper objectMapper = new ObjectMapper();
		try (BufferedReader br = Files.newBufferedReader(Paths.get(file))) {
            String line;
			logger.info("Processing events, this may take some time depending on file size");
            while ((line = br.readLine()) != null) {
                processJson(processedEventsMap, objectMapper, line);				
            }
			//If we are in batch mode then persist whatever is stored in memory
			if (!writeToDbList.isEmpty())
				endOfBatchPersist();

			logger.info("Finished processing events");

			if (processedEventsMap.size() > 0)
				logger.warn("Finished processing logged file and found {} unmatched events", processedEventsMap.size());

        } catch (IOException e) {
            logger.error("Failed to process file {}", file);
			logger.error("IOException", e);
        }
        
    }

	public void processJson(Map<String, LogEntry> processedEventsMap, ObjectMapper objectMapper, String json)
			throws JsonProcessingException {
		LogEntry entry = objectMapper.readValue(json,LogEntry.class);
		/**We are assuming only 2 event states for each ID (START/FINISH)
		If the map already contains the key they we know we have received
		both events, and can proceed with transforming and uploading to the 
		DB*/
		if(processedEventsMap.containsKey(entry.getId())){
			logger.debug("START and FINISH Event received for event id {}" ,entry.getId());
			calculateEventDuration(processedEventsMap,entry);
			persist(entry);
			processedEventsMap.remove(entry.getId());
		} else {
			processedEventsMap.put(entry.getId(), entry);
		}
	}

    @Override
    public void persist(Entry entry) {
		if (writeToDbList.size() < batchEventListSize) {
			writeToDbList.add(entryToEvent.map(entry));
		} else {
			eventService.batchSaveAll(writeToDbList);
			writeToDbList = new ArrayList<>();
			writeToDbList.add(entryToEvent.map(entry));
		}
		logger.debug("Writing Event ID: {} to the DB!" , ((LogEntry) entry).getId());       
    }

	public void endOfBatchPersist() {
		logger.info("Persisting final batch of events to the DB");
		eventService.batchSaveAll(writeToDbList);       
    }

    public void calculateEventDuration(Map<String, LogEntry> processedEventsMap, LogEntry entry) {
		Long firstEntryTime = processedEventsMap.get(entry.getId()).getTimestamp();
		Long duration = Math.abs( firstEntryTime - entry.getTimestamp()); 
		logger.debug("calculateEventDuration for id {}: abs({} - {}) = {} ",entry.getId(),firstEntryTime, entry.getTimestamp(), duration);
		entry.setTimestamp(duration);
	}


	public int getBatchEventListSize() {
		return this.batchEventListSize;
	}

	public void setBatchEventListSize(int batchEventListSize) {
		this.batchEventListSize = batchEventListSize;
	}

}

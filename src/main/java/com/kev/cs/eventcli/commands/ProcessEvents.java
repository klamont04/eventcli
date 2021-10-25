package com.kev.cs.eventcli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.kev.cs.eventcli.processor.LogEntryEventProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Parameters(commandNames = ProcessEvents.COMMAND,
        commandDescription = "Process the file and persist to DB")
public class ProcessEvents implements Command {
    public static final Logger logger = LoggerFactory.getLogger(ProcessEvents.class);
    public static final String COMMAND = "process_events";

    private LogEntryEventProcessor processor;

    public ProcessEvents(LogEntryEventProcessor processor) {
        this.processor = processor;
    }

    @Parameter(names = "--file-name", required = true, description = "File to be loaded - must include full path to file")
    private String file;
    
    @Override
    public String commandName() {
        return COMMAND;
    }

    @Override
    public void run() {
        logger.info("Running {} with parameters --file-name= {}", COMMAND, file);
        processor.process(file);
    }

    public String getFile() {
        return file;
    }
    
}

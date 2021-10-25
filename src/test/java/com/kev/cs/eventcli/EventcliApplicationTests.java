package com.kev.cs.eventcli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.kev.cs.eventcli.commands.Command;
import com.kev.cs.eventcli.commands.ProcessEvents;
import com.kev.cs.eventcli.processor.LogEntryEventProcessor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventcliApplicationTests {
	@Autowired
	private List<Command> commands;

	@Test
	void testProcessEventCommandLineParams() {
		String[] args = { "process_events", "--file-name", "\\path\\to\\logfile.txt" };
		JCommander.Builder builder = JCommander.newBuilder().programName("event-info");
		commands.forEach(builder::addCommand);
		JCommander jCommander = builder.build();
		jCommander.parse(args);
		Command command = commands.get(0);
		assertEquals("process_events", command.commandName());
		assertEquals("\\path\\to\\logfile.txt", ((ProcessEvents) command).getFile());
	}

	@Test
	void testProcessEventMissingFilename() {
		String[] args = { "process_events" };
		JCommander.Builder builder = JCommander.newBuilder().programName("event-info");
		commands.forEach(builder::addCommand);
		JCommander jCommander = builder.build();

		assertThrows(ParameterException.class, () -> {
			jCommander.parse(args);
		});
	}

	@Test
	void processEventsCommand() {
		LogEntryEventProcessor processor = mock(LogEntryEventProcessor.class);
		ProcessEvents command = new ProcessEvents(processor);
		String[] args = { "process_events", "--file-name", "\\path\\to\\logfile.txt" };
		JCommander.Builder builder = JCommander.newBuilder().programName("event-info").addCommand(command);
		JCommander jCommander = builder.build();
		jCommander.parse(args);
		command.run();
		verify(processor, times(1)).process(anyString());

	}

}

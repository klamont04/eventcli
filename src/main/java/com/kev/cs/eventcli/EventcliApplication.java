package com.kev.cs.eventcli;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.beust.jcommander.JCommander;
import com.kev.cs.eventcli.commands.Command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class EventcliApplication implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(EventcliApplication.class);

	@Autowired
    private List<Command> commands;

	public static void main(String[] args) {
		
		ConfigurableApplicationContext ctx = SpringApplication.run(EventcliApplication.class, args);
		ctx.close();
	}

	@Override
	public void run(String... args) throws IOException {
		logger.info("Starting EventCli");
		JCommander.Builder builder = JCommander.newBuilder().programName("event-info");
		commands.forEach(builder::addCommand);
		JCommander jCommander = builder.build();
		jCommander.parse(args);
		logger.info("Finished parsing command line arguments");

		Optional<Command> command = commands.stream().filter(cmd -> cmd.commandName().equals(jCommander.getParsedCommand()))
				.findFirst();

		if (command.isPresent()) {
			command.get().run();
		} else {
			jCommander.usage();
		}
	}

}

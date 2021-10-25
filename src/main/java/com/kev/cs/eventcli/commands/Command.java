package com.kev.cs.eventcli.commands;

public interface Command {
    String commandName();
    void run();
}

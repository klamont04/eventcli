package com.kev.cs.eventcli.processor;

import java.io.IOException;

import com.kev.cs.eventcli.models.Entry;

public interface EventProcessor {
    void process(String file) throws IOException;
    void persist(Entry entry);
}

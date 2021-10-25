package com.kev.cs.eventcli.mapper;

import com.kev.cs.eventcli.models.Entry;
import com.kev.cs.eventcli.models.Event;

public interface EntryToEventMapper {
    Event map(Entry entry);
}

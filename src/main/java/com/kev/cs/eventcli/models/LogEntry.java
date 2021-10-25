package com.kev.cs.eventcli.models;

import org.springframework.lang.NonNull;

public class LogEntry implements Entry{
    @NonNull
    private String id;
    @NonNull
    private String state;
    @NonNull
    private Long timestamp;
    private String type;
    private String host;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    
}

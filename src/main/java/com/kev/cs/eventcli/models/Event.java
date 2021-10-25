package com.kev.cs.eventcli.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Event {
    @Id
    @Column(name="EVENT_ID", unique=true)
    private String eventId;
    private Long duration;
    private String type;
    private String host;
    private boolean alert;

    public String getEventId() {
        return this.eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
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

    public boolean isAlert() {
        return this.alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    
}

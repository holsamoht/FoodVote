package com.example.local1.foodvote;

import java.util.List;

public class Event {
    String objectId;
    String eventName;
    String eventOwner;
    List<String> eventParticipants;

    public Event(String objectId, String eventName, String eventOwner) {
        this.objectId = objectId;
        this.eventName = eventName;
        this.eventOwner = eventOwner;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public String getEventName() {
        return this.eventName;
    }

    public String getEventOwner() {
        return this.eventOwner;
    }
}

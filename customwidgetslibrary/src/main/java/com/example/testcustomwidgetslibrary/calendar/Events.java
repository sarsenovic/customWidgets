package com.example.testcustomwidgetslibrary.calendar;

import com.example.testcustomwidgetslibrary.calendar.event.Event;

import java.util.List;

public class Events {
    private final List<Event> eventsList;
    private final long eventTimeInMillis;

    public Events(List<Event> eventsList, long eventTimeInMillis) {
        this.eventsList = eventsList;
        this.eventTimeInMillis = eventTimeInMillis;
    }

    List<Event> getEventsList() {
        return eventsList;
    }

    long getEventTimeInMillis() {
        return eventTimeInMillis;
    }
}

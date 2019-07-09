package com.sasaarsenovic.customwidgetslibrary.calendar.event;

import android.support.annotation.Nullable;

public class Event {
    private int eventColor;
    private long eventTimeInMillis;
    private Object data;

    public Event(int eventColor, long eventTimeInMillis) {
        this.eventColor = eventColor;
        this.eventTimeInMillis = eventTimeInMillis;
    }

    public Event(int eventColor, long eventTimeInMillis, Object data) {
        this.eventColor = eventColor;
        this.eventTimeInMillis = eventTimeInMillis;
        this.data = data;
    }

    public int getEventColor() {
        return eventColor;
    }

    public long getEventTimeInMillis() {
        return eventTimeInMillis;
    }

    @Nullable
    public Object getData() {
        return data;
    }
}

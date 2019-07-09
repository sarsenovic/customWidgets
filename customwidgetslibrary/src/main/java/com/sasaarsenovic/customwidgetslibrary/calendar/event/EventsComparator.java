package com.sasaarsenovic.customwidgetslibrary.calendar.event;

import java.util.Comparator;

public class EventsComparator implements Comparator<Event> {
    //if o1 > o2, it returns positive number
    //if o1 < o2, it returns negative number
    //if o1 == o2, it returns 0
    @Override
    public int compare(Event o1, Event o2) {
        return o1.getEventTimeInMillis() < o2.getEventTimeInMillis() ? -1 : o1.getEventTimeInMillis() == o2.getEventTimeInMillis() ? 0 : 1;
    }
}

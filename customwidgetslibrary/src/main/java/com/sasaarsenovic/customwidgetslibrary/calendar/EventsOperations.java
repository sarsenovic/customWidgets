package com.sasaarsenovic.customwidgetslibrary.calendar;

import com.sasaarsenovic.customwidgetslibrary.calendar.Events;
import com.sasaarsenovic.customwidgetslibrary.calendar.event.Event;
import com.sasaarsenovic.customwidgetslibrary.calendar.event.EventsComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EventsOperations {
    private Map<String, List<Events>> eventsMapForMonthAndYear = new HashMap<>();
    private Comparator<Event> eventComparator = new EventsComparator();
    private Calendar eventsCalendar;

    public EventsOperations(Calendar eventsCalendar) {
        this.eventsCalendar = eventsCalendar;
    }

    void addEvent(Event event) {
        eventsCalendar.setTimeInMillis(event.getEventTimeInMillis());
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<Events> monthEvents = eventsMapForMonthAndYear.get(key);
        if (monthEvents == null) {
            monthEvents = new ArrayList<>();
        }

        Events targetDayEvents = getEventDayEvent(event.getEventTimeInMillis());
        if (targetDayEvents == null) {
            List<Event> events = new ArrayList<>();
            events.add(event);
            monthEvents.add(new Events(events, event.getEventTimeInMillis()));
        } else {
            targetDayEvents.getEventsList().add(event);
        }
        eventsMapForMonthAndYear.put(key, monthEvents);
    }

    void removeAllEvents() {
        eventsMapForMonthAndYear.clear();
    }

    void removeEvents(List<Event> events) {
        int count = events.size();
        for (int i = 0; i < count; i++) {
            removeEvent(events.get(i));
        }
    }

    void removeEvent(Event event) {
        eventsCalendar.setTimeInMillis(event.getEventTimeInMillis());
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<Events> eventsForMonthAndYear = eventsMapForMonthAndYear.get(key);
        if (eventsForMonthAndYear != null) {
            Iterator<Events> eventsForMonthYrItr = eventsForMonthAndYear.iterator();
            while(eventsForMonthYrItr.hasNext()) {
                Events events = eventsForMonthYrItr.next();
                int indexOfEvent = events.getEventsList().indexOf(event);
                if (indexOfEvent >= 0) {
                    if (events.getEventsList().size() == 1) {
                        eventsForMonthYrItr.remove();
                    } else {
                        events.getEventsList().remove(indexOfEvent);
                    }
                    break;
                }
            }
            if (eventsForMonthAndYear.isEmpty()) {
                eventsMapForMonthAndYear.remove(key);
            }
        }
    }

    void removeEventByEpochMillis(long epochMillis) {
        eventsCalendar.setTimeInMillis(epochMillis);
        int dayInMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<Events> eventsForMonthAndYear = eventsMapForMonthAndYear.get(key);
        if (eventsForMonthAndYear != null) {
            Iterator<Events> calendarDayEventIterator = eventsForMonthAndYear.iterator();
            while (calendarDayEventIterator.hasNext()) {
                Events next = calendarDayEventIterator.next();
                eventsCalendar.setTimeInMillis(next.getEventTimeInMillis());
                int dayInMonthFromCache = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                if (dayInMonthFromCache == dayInMonth) {
                    calendarDayEventIterator.remove();
                    break;
                }
            }
            if (eventsForMonthAndYear.isEmpty()) {
                eventsMapForMonthAndYear.remove(key);
            }
        }
    }

    void addEvents(List<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            addEvent(events.get(i));
        }
    }

    List<Event> getEventsFor(long epochMillis) {
        Events events = getEventDayEvent(epochMillis);
        if (events == null) {
            return new ArrayList<>();
        } else {
            return events.getEventsList();
        }
    }

    List<Events> getEventsForMonthAndYear(int month, int year){
        return eventsMapForMonthAndYear.get(year + "_" + month);
    }

    List<Event> getEventsForMonth(long eventTimeInMillis){
        eventsCalendar.setTimeInMillis(eventTimeInMillis);
        String keyForCalendarEvent = getKeyForCalendarEvent(eventsCalendar);
        List<Events> events = eventsMapForMonthAndYear.get(keyForCalendarEvent);
        List<Event> allEventsForMonth = new ArrayList<>();
        if (events != null) {
            for(Events eve : events){
                if (eve != null) {
                    allEventsForMonth.addAll(eve.getEventsList());
                }
            }
        }
        Collections.sort(allEventsForMonth, eventComparator);
        return allEventsForMonth;
    }

    private Events getEventDayEvent(long eventTimeInMillis) {
        eventsCalendar.setTimeInMillis(eventTimeInMillis);
        int dayInMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
        String keyForCalendarEvent = getKeyForCalendarEvent(eventsCalendar);
        List<Events> monthAndYearEvents = eventsMapForMonthAndYear.get(keyForCalendarEvent);
        if (monthAndYearEvents != null) {
            for (Events events : monthAndYearEvents) {
                eventsCalendar.setTimeInMillis(events.getEventTimeInMillis());
                int dayInMonthFromCache = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                if (dayInMonthFromCache == dayInMonth) {
                    return events;
                }
            }
        }
        return null;
    }

    private String getKeyForCalendarEvent(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.MONTH);
    }
}

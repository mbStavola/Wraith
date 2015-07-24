package com.kevinmost.wraith.event;

import java.util.List;

public class RingInfo implements IRingInfo {

  private final List<Event> events;

  public RingInfo(List<Event> events) {
    mergeOverlappingEvents(events);
    this.events = events;
  }

  private static void mergeOverlappingEvents(List<Event> events) {
    for (int i = 0; i < events.size(); i++) {
      // If the next event starts before this one ends...
      while (i < events.size() - 1 && events.get(i).end >= events.get(i + 1).start) {
        // ... merge the two by setting this event's end time to the next event's end time...
        events.get(i).end = Math.max(events.get(i).end, events.get(i + 1).end);
        // ... and then remove the (now "eaten up") 2nd event
        events.remove(i + 1);
      }
    }
  }

  @Override
  public List<Event> getEvents() {
    return events;
  }
}

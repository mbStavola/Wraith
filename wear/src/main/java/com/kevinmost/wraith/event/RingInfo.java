package com.kevinmost.wraith.event;

import android.support.annotation.NonNull;
import org.joda.time.Interval;

import java.util.List;

public class RingInfo implements IRingInfo {

  private final List<Event> events;

  public RingInfo(@NonNull List<Event> events) {
    mergeOverlappingEvents(events);
    this.events = events;
  }

  private static void mergeOverlappingEvents(List<Event> events) {
    for (int i = 0; i < events.size() - 1; i++) {
      final Interval first = events.get(i).interval;
      final Interval second = events.get(i + 1).interval;
      if (!(first.overlaps(second) || first.abuts(second))) {
        continue;
      }
      // If the next event starts before this one ends, remove the existing second interval
      // and stretch the first one to the entire span.
      events.remove(i+1);
      events.get(i).interval =
          first.withEndMillis(Math.max(first.getEndMillis(), second.getEndMillis()));
    }
  }

  @NonNull
  @Override
  public List<Event> getEvents() {
    return events;
  }
}

package com.kevinmost.wraith.event;

import org.joda.time.Interval;

public class Event {
  public Interval interval;
  public long color;

  /**
   * @param start Start-time of this event in milliseconds since Unix epoch.
   * @param end End-time of this event in milliseconds since Unix epoch
   * @param color TODO I have no idea what this is...
   */
  public Event(long start, long end, long color) {
    this.interval = new Interval(start, end);
    this.color = color;
  }
}

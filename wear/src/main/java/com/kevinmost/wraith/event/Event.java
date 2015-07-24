package com.kevinmost.wraith.event;

public class Event {
  public long start;
  public long end;
  public long color;

  public Event(long start, long end, long color) {
    this.start = start;
    this.end = end;
    this.color = color;
  }
}

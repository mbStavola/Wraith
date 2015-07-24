package com.kevinmost.wraith.event;

import java.util.List;

public interface IRingInfo {
  /**
   * @return A list of events to draw. These events should all be "merged"; that is, if events
   * overlap, they should be returned as one large event. See {@link RingInfo}'s constructor for an
   * example.
   */
  public List<Event> getEvents();
}

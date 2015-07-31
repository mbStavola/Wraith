package com.kevinmost.wraith;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.Log;
import com.kevinmost.wraith.event.IRingInfo;
import com.kevinmost.wraith.event.RingInfo;
import com.kevinmost.wraith.hand.WraithHand;
import com.kevinmost.wraith.event.Event;
import com.kevinmost.wraith.ring.WraithRing;

import java.util.Calendar;
import java.util.List;

public class WraithFaceService extends BaseFaceService {

  @Override
  public Engine onCreateEngine() {
    return new Engine();
  }

  class Engine extends BaseFaceService.Engine {

    private IRingInfo calendarRingInfo;

    @Override
    public void onDraw(Canvas canvas, Rect bounds) {
      super.onDraw(canvas, bounds);

      // Erase the canvas' last state
      canvas.drawColor(0, PorterDuff.Mode.CLEAR);

      drawWatchTicks(canvas);

      calendar.setTimeInMillis(System.currentTimeMillis());

      if (calendarRingInfo != null) {
        WraithRing.OUTER_RING.drawToCanvas(canvas, calendarRingInfo);
      }

      final float seconds =
          calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000F;
      final float minutes = calendar.get(Calendar.MINUTE) + seconds / 60F;
      final float hours = calendar.get(Calendar.HOUR) + minutes / 60F;

      WraithHand.HOUR.drawToCanvas(canvas, hours);
      WraithHand.MINUTE.drawToCanvas(canvas, minutes);

      // The circle that is sandwiched between the min/hour hands and second hand on a real watch.
      canvas.drawCircle(WatchParams.centerX, WatchParams.centerY, 7, WraithHand.HOUR.getPaint());
      canvas.drawCircle(WatchParams.centerX, WatchParams.centerY, 3, WraithHand.SECOND.getPaint());

      if (!isInAmbientMode()) {
        // We don't want seconds to display in-between integer values. It looks weird.
        WraithHand.SECOND.drawToCanvas(canvas, (int) seconds);
      }
    }

    private void drawWatchTicks(Canvas canvas) {
      for (int i = 0; i < 60; i++) {
        final float angle = WraithHand.TWO_PI * i / 60;
        final float angleSin = (float) Math.sin(angle);
        final float angleCos = (float) Math.cos(angle);
        final float tickLength = (i % 5 == 0) ? 15 : 30;
        canvas.drawLine(
            WatchParams.centerX + (angleSin * tickLength), WatchParams.centerY + (-angleCos * tickLength),
            WatchParams.centerX + (angleSin * WatchParams.centerX), WatchParams.centerY + (-angleCos * WatchParams.centerY),
            Paints.PAINT_FACE_TICKS
        );
      }
    }

    @Override
    public void onCalendarEventsLoaded(List<Event> events) {
      if (events == null) {
        return;
      }
      Log.e("WRAITH", events.size() + " calendar events obtained during refresh!");
      calendarRingInfo = new RingInfo(events);
      postInvalidate();
    }
  }
}

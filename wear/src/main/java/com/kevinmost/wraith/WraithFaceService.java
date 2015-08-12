package com.kevinmost.wraith;

import android.graphics.Canvas;
import android.graphics.Paint;
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

    private final int colorMonokaiBlack = getResources().getColor(R.color.monokai_black);

    private IRingInfo calendarRingInfo;

    @Override
    public void onDraw(Canvas canvas, Rect bounds) {
      super.onDraw(canvas, bounds);

      // Erase the canvas' last state
      canvas.drawColor(colorMonokaiBlack);

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

        final Paint paint;
        final float tickDistanceFromEdge;
        if (i % 15 == 0) {
          paint = Paints.PAINT_FACE_MAJOR_TICKS;
          tickDistanceFromEdge = 30;
        } else if (i % 5 == 0) {
          paint = Paints.PAINT_FACE_TICKS;
          tickDistanceFromEdge = 15;
        } else {
          paint = Paints.PAINT_FACE_TICKS;
          tickDistanceFromEdge = 5;
        }

        final float angle = WraithHand.TAU * i / 60;
        final float unitOffsetX = (float) Math.sin(angle);
        final float unitOffsetY = (float) -Math.cos(angle);
        final float tickDistance1 = WatchParams.centerX - tickDistanceFromEdge;
        final float x1 = WatchParams.centerX + (unitOffsetX * tickDistance1);
        final float y1 = WatchParams.centerY + (unitOffsetY * tickDistance1);
        final float x2 = WatchParams.centerX + (unitOffsetX * WatchParams.centerX);
        final float y2 = WatchParams.centerY + (unitOffsetY * WatchParams.centerY);
        canvas.drawLine(x1, y1, x2, y2, paint);
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

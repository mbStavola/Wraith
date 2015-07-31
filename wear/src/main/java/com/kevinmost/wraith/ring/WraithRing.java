package com.kevinmost.wraith.ring;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.kevinmost.wraith.WatchParams;
import com.kevinmost.wraith.event.Event;
import com.kevinmost.wraith.event.IRingInfo;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public enum WraithRing implements IWraithRing {
  OUTER_RING(100) {
    @Override
    protected Paint vendPaint() {
      final Paint paint = vendPaintWithSaneDefaults();
      paint.setARGB(255, 255, 179, 71);
      return paint;
    }
  },
  INNER_RING(70) {
    @Override
    protected Paint vendPaint() {
      final Paint paint = vendPaintWithSaneDefaults();
      paint.setARGB(255, 255, 179, 71);
      return paint;
    }
  },
  ;

  private final int ringRadius;
  private final Paint paint;

  WraithRing(int ringRadius) {
    this.paint = vendPaint();
    this.ringRadius = ringRadius;
  }

  protected abstract Paint vendPaint();

  /**
   * @param canvas The canvas from your onDraw() method.
   * @param ringInfo The information to draw into this ring.
   */
  @Override
  public final void drawToCanvas(Canvas canvas, IRingInfo ringInfo) {
    for (Event event : ringInfo.getEvents()) {
      final DateTime start = event.interval.getStart();
      final float startHours = start.getMinuteOfDay() / 60F;

      final Duration duration = event.interval.toDuration();
      final float durationInHours = duration.getStandardMinutes() / 60F;

      final float arcStartPos = oClockCoordToDrawableFloat(startHours);
      final float arcSweep = durationInHours * 30;

      canvas.drawArc(
          WatchParams.centerX - ringRadius, WatchParams.centerY - ringRadius,
          WatchParams.centerX + ringRadius, WatchParams.centerY + ringRadius,
          arcStartPos, arcSweep,
          false, paint);
    }
  }

  private static Paint vendPaintWithSaneDefaults() {
    final Paint paint = new Paint();
    paint.setStyle(Paint.Style.STROKE);
    paint.setAntiAlias(true);
    paint.setStrokeWidth(20);
    paint.setStrokeCap(Paint.Cap.BUTT);
    return paint;
  }

  private static float oClockCoordToDrawableFloat(float oClock) {
    // minus 90 because on the canvas, 0 is at 3 o'clock.
    return oClock * 30 - 90;
  }
}

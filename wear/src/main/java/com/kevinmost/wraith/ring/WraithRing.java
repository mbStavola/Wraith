package com.kevinmost.wraith.ring;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.kevinmost.wraith.event.Event;
import com.kevinmost.wraith.event.IRingInfo;

public enum WraithRing implements IWraithRing {
  OUTER_RING(100) {
    @Override
    protected Paint vendPaint() {
      final Paint paint = new Paint(BASE_PAINT);
      paint.setARGB(255, 255, 179, 71);
      return paint;
    }
  },
  INNER_RING(70) {
    @Override
    protected Paint vendPaint() {
      final Paint paint = new Paint(BASE_PAINT);
      paint.setARGB(255, 255, 179, 71);
      return paint;
    }
  },
  ;

  private static final Paint BASE_PAINT;

  static {
    BASE_PAINT = new Paint();
    BASE_PAINT.setStyle(Paint.Style.STROKE);
    BASE_PAINT.setAntiAlias(true);
    BASE_PAINT.setStrokeWidth(20);
    BASE_PAINT.setStrokeCap(Paint.Cap.ROUND);
  }

  private final int ringRadius;
  private final Paint paint;

  WraithRing(int ringRadius) {
    this.paint = vendPaint();
    this.ringRadius = ringRadius;
  }

  protected abstract Paint vendPaint();

  /**
   * @param canvas The canvas from your onDraw() method.
   * @param centerX The center x coord of this watch-face.
   * @param centerY The center y coord of this watch-face.
   * @param ringInfo The information to draw into this ring.
   */
  @Override
  public final void drawToCanvas(Canvas canvas, float centerX, float centerY,
      IRingInfo ringInfo) {
    for (Event event : ringInfo.getEvents()) {
      final long start = event.start;
      final long end = event.end;

      long length = end - start;
      // The length of the event could look like it's negative if the event goes from, say, 11PM
      // to 1AM. 1 - 23 = -22, so add 24 so it's a proper 2-hour-long event.
      if (length < 0) {
        length += 24;
      }

      final float arcStartPos = start * 30 - 90; // - 90 because on the canvas, 0 is at 3 o'clock.
      final float arcSweep = length * 30;

      canvas.drawArc(
          centerX - ringRadius, centerY - ringRadius,
          centerX + ringRadius, centerY + ringRadius,
          arcStartPos, arcSweep,
          false, paint);
    }
  }
}

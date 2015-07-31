package com.kevinmost.wraith.hand;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.kevinmost.wraith.WatchParams;

public enum WraithHand implements IWraithHand {
  HOUR(12, 60F) {
    @Override
    protected Paint vendPaint() {
      final Paint paint = new Paint();
      paint.setARGB(255, 255, 255, 255);
      paint.setStrokeWidth(6);
      paint.setStrokeCap(Paint.Cap.ROUND);
      paint.setAntiAlias(true);
      return paint;
    }
  },
  MINUTE(60, 10F) {
    @Override
    protected Paint vendPaint() {
      // For now they use the same paint
      return new Paint(HOUR.vendPaint());
    }
  },
  SECOND(60, 10, true) {
    @Override
    protected Paint vendPaint() {
      final Paint paint = new Paint();
      paint.setARGB(255, 200, 0, 0);
      paint.setAntiAlias(true);
      paint.setStrokeWidth(2);
      paint.setStrokeCap(Paint.Cap.ROUND);
      return paint;
    }
  }
  ;

  public static final float TWO_PI = ((float) (2 * Math.PI));

  private final Paint paint;
  private final int numberOfUnits;
  private final float distanceFromWatchBorder;
  private final boolean overshoot;

  WraithHand(int numberOfUnits, float distanceFromWatchBorder) {
    this(numberOfUnits, distanceFromWatchBorder, false);
  }

  WraithHand(int numberOfUnits, float distanceFromWatchBorder, boolean overshoot) {
    this.paint = vendPaint();
    this.numberOfUnits = numberOfUnits;
    this.distanceFromWatchBorder = distanceFromWatchBorder;
    this.overshoot = overshoot;
  }

  protected abstract Paint vendPaint();

  @Override
  public void drawToCanvas(Canvas canvas, float valueToDraw) {
    final float rotation = valueToDraw / numberOfUnits * TWO_PI;

    // The offset in the x direction if this hand had a length of 1px
    final float unitOffsetX = ((float) Math.sin(rotation));
    // The offset in the y direction if this hand had a length of 1px
    final float unitOffsetY = (float) -Math.cos(rotation);

    final float handLength = WatchParams.centerX - distanceFromWatchBorder;
    final float centerOffsetX = handLength * unitOffsetX;
    final float centerOffsetY = handLength * unitOffsetY;

    final float lineStartX;
    final float lineStartY;
    if (overshoot) {
      lineStartX = WatchParams.centerX - (centerOffsetX / 10F);
      lineStartY = WatchParams.centerY - (centerOffsetY / 10F);
    } else {
      lineStartX = WatchParams.centerX;
      lineStartY = WatchParams.centerY;
    }
    canvas.drawLine(
        lineStartX, lineStartY,
        WatchParams.centerX + centerOffsetX, WatchParams.centerY + centerOffsetY,
        paint);
  }

  @Override
  public Paint getPaint() {
    return paint;
  }
}

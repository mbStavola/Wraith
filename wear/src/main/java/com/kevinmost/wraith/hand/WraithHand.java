package com.kevinmost.wraith.hand;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.kevinmost.wraith.Paints;
import com.kevinmost.wraith.WatchParams;

public enum WraithHand implements IWraithHand {
  HOUR(Paints.PAINT_HAND_HOUR_MINUE, 12, 60F),
  MINUTE(Paints.PAINT_HAND_HOUR_MINUE, 60, 10F),
  SECOND(Paints.PAINT_HAND_SECOND, 60, 10, true)
  ;

  public static final float TAU = ((float) (2 * Math.PI));

  private final Paint paint;
  private final int numberOfUnits;
  private final float distanceFromWatchBorder;
  private final boolean overshoot;

  WraithHand(Paint paint, int numberOfUnits, float distanceFromWatchBorder) {
    this(paint, numberOfUnits, distanceFromWatchBorder, false);
  }

  WraithHand(Paint paint, int numberOfUnits, float distanceFromWatchBorder, boolean overshoot) {
    this.paint = paint;
    this.numberOfUnits = numberOfUnits;
    this.distanceFromWatchBorder = distanceFromWatchBorder;
    this.overshoot = overshoot;
  }

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

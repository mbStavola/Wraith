package com.kevinmost.wraith;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import java.util.Calendar;

public class WraithFaceService extends BaseFaceService {
  @Override
  public Engine onCreateEngine() {
    return new Engine();
  }

  private class Engine extends BaseFaceService.Engine {
    private static final float TWO_PI = ((float) (Math.PI * 2F));

    private final Paint hourMinutePaint = new Paint(Paint.ANTI_ALIAS_FLAG) {{
      setARGB(255, 255, 255, 255);
      setStrokeCap(Cap.ROUND);
      setStrokeWidth(4);
    }};

    private final Paint secondPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {{
      setARGB(255, 200, 0, 0);
      setStrokeCap(Cap.ROUND);
      setStrokeWidth(2);
    }};

    private final Paint mintGreenArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {{
      setARGB(255, 152, 255, 152);
      setStrokeWidth(20);
      setStyle(Paint.Style.STROKE);
      setStrokeCap(Paint.Cap.ROUND);
    }};

    private final Paint orangeArcPaint = new Paint(mintGreenArcPaint) {{
      setARGB(255, 255, 179, 71);
    }};


    @Override
    public void onAmbientModeChanged(boolean inAmbientMode) {
      super.onAmbientModeChanged(inAmbientMode);
      if (isLowBitAmbient) {
        final boolean antiAlias = !inAmbientMode;
        hourMinutePaint.setAntiAlias(antiAlias);
      }
      invalidate();
      updateTimer();
    }

    private float centerX;
    private float centerY;

    private final Hand hourHand = new Hand(hourMinutePaint, 12, 60);
    private final Hand minuteHand = new Hand(hourMinutePaint, 60, 10);
    private final Hand secondHand = new Hand(secondPaint, 60, 10);

    @Override
    public void onDraw(Canvas canvas, Rect bounds) {
      super.onDraw(canvas, bounds);

      // Erase the canvas' last state
      canvas.drawColor(0, PorterDuff.Mode.CLEAR);

      calendar.setTimeInMillis(System.currentTimeMillis());

      centerX = bounds.width() / 2F;
      centerY = bounds.height() / 2F;

      drawRing(canvas, 100F, 2F, 4.5F, mintGreenArcPaint);
      drawRing(canvas, 70F, 3.5F, 6F, orangeArcPaint);
      drawRing(canvas, 70F, 8F, 1.5F, orangeArcPaint);

      final float seconds =
          calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000F;
      final float minutes = calendar.get(Calendar.MINUTE) + seconds / 60F;
      final float hours = calendar.get(Calendar.HOUR) + minutes / 60F;

      hourHand.drawToCanvas(canvas, hours);
      minuteHand.drawToCanvas(canvas, minutes);

      // The circle that is sandwiched between the min/hour hands and second hand on a real watch.
      canvas.drawCircle(centerX, centerY, 7, hourMinutePaint);
      canvas.drawCircle(centerX, centerY, 3, secondPaint);

      if (!isInAmbientMode()) {
        // We don't want seconds to display in-between integer values. It looks weird.
        secondHand.drawToCanvas(canvas, (int) seconds, true);
      }
    }

    private void drawRing(Canvas canvas, float outerRadius, float hourStart, float hourEnd,
        Paint paint) {
      // Multiply by 30 to transform the hour (0,12) range to a degree (0,360) range.
      // Minus 90 because the 0 position on the canvas drawArc method is at 3 o' clock.
      final float arcStartPos = hourStart * 30 - 90;
      if (hourEnd < hourStart) {
        hourEnd += 12;
      }
      final float arcEndPos = (hourEnd * 30 - 90) - arcStartPos;
      canvas.drawArc(
          centerX - outerRadius, centerY - outerRadius,
          centerX + outerRadius, centerY + outerRadius,
          arcStartPos, arcEndPos,
          false, paint);
    }

    class Hand {
      final Paint paint;
      final int numberOfUnits;
      final float distanceFromWatchBorder;

      /**
       * @param paint The paint with which to draw this hand
       * @param numberOfUnits Number of units in this type of value. For example, for hour this
       * would be 12, for minutes 60, for seconds 60, etc
       * @param distanceFromWatchBorder The number of pixels from the edge of the watch that this
       * hand should project out to.
       */
      Hand(Paint paint, int numberOfUnits, float distanceFromWatchBorder) {
        this.paint = paint;
        this.numberOfUnits = numberOfUnits;
        this.distanceFromWatchBorder = distanceFromWatchBorder;
      }

      void drawToCanvas(Canvas canvas, float valueToDraw) {
        drawToCanvas(canvas, valueToDraw, false);
      }

      void drawToCanvas(Canvas canvas, float valueToDraw, boolean overshoot) {
        final float rotation = valueToDraw / numberOfUnits * TWO_PI;

        // The offset in the x direction if this hand had a length of 1px
        final float unitOffsetX = ((float) Math.sin(rotation));
        // The offset in the y direction if this hand had a length of 1px
        final float unitOffsetY = (float) -Math.cos(rotation);

        final float handLength = centerX - distanceFromWatchBorder;
        final float centerOffsetX = handLength * unitOffsetX;
        final float centerOffsetY = handLength * unitOffsetY;

        final float lineStartX;
        final float lineStartY;
        if (overshoot) {
          lineStartX = centerX - (centerOffsetX / 10F);
          lineStartY = centerY - (centerOffsetY / 10F);
        } else {
          lineStartX = centerX;
          lineStartY = centerY;
        }
        canvas.drawLine(
            lineStartX, lineStartY,
            centerX + centerOffsetX, centerY + centerOffsetY,
            paint);
      }
    }

  }
}

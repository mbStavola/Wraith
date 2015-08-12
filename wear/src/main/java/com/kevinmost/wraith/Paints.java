package com.kevinmost.wraith;

import android.graphics.Paint;
import android.graphics.PathEffect;

public class Paints {
  public static final Paint PAINT_FACE_TICKS = new Paint() {{
    setAntiAlias(true);
    setARGB(170, 170, 170, 170);
    setStrokeCap(Cap.BUTT);
    setStrokeWidth(3);
    setStyle(Style.STROKE);
  }};
  public static final Paint PAINT_FACE_MAJOR_TICKS = new Paint(PAINT_FACE_TICKS) {{
    final int alpha = getAlpha();
    setColor(Colors.MONOKAI_MAGENTA);
    setAlpha(alpha);
    setStrokeWidth(5);
  }};

  public static final Paint PAINT_HAND_HOUR_MINUE = new Paint() {{
    setAntiAlias(true);
    setARGB(255, 255, 255, 255);
    setStrokeCap(Paint.Cap.ROUND);
    setStrokeWidth(6);
    setStyle(Style.STROKE);
  }};

  public static final Paint PAINT_HAND_SECOND = new Paint() {{
    setAntiAlias(true);
    setColor(Colors.MONOKAI_MAGENTA);
    setStrokeCap(Paint.Cap.ROUND);
    setStrokeWidth(2);
    setStyle(Style.STROKE);
  }};
}

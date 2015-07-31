package com.kevinmost.wraith;

import android.graphics.Paint;
import android.graphics.PathEffect;

public class Paints {
  public static final Paint PAINT_FACE_TICKS = new Paint() {{
    setAntiAlias(true);
    setARGB(200, 255, 255, 255);
    setStrokeCap(Cap.BUTT);
    setStrokeWidth(3);
    setStyle(Style.STROKE);
  }};
}

package com.kevinmost.wraith.ring;

import android.graphics.Canvas;
import com.kevinmost.wraith.event.IRingInfo;

public interface IWraithRing {
  public void drawToCanvas(Canvas canvas, float centerX, float centerY, IRingInfo ringInfo);
}

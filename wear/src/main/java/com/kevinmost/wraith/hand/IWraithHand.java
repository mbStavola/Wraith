package com.kevinmost.wraith.hand;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface IWraithHand {
  public void drawToCanvas(Canvas canvas, float valueToDraw);
  public Paint getPaint();
}

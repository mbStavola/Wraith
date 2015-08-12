package com.kevinmost.wraith;

import android.support.annotation.ColorRes;

public class Colors {
  public static final int MONOKAI_BLACK = getColor(R.color.monokai_black);
  public static final int MONOKAI_MAGENTA = getColor(R.color.monokai_magenta);
  public static final int MONOKAI_BLUE = getColor(R.color.monokai_blue);
  public static final int MONOKAI_GREEN = getColor(R.color.monokai_green);
  public static final int MONOKAI_ORANGE = getColor(R.color.monokai_orange);

  private static int getColor(@ColorRes int color) {
    return Wraith.getAppContext().getResources().getColor(color);
  }
}

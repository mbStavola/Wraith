package com.kevinmost.wraith;

import android.app.Application;
import android.content.Context;

public class Wraith extends Application {

  private static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    Wraith.context = getApplicationContext();
  }

  public static Context getAppContext() {
    return Wraith.context;
  }
}

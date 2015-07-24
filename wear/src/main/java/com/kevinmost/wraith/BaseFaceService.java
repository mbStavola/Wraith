package com.kevinmost.wraith;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.Gravity;
import android.view.SurfaceHolder;

import java.util.Calendar;
import java.util.TimeZone;

public abstract class BaseFaceService extends CanvasWatchFaceService {
  @Override
  public Engine onCreateEngine() {
    return new Engine();
  }

  public class Engine extends CanvasWatchFaceService.Engine {
    protected boolean isLowBitAmbient;
    protected boolean hasBurnInProtection;

    private static final int MSG_UPDATE_TIME = 0;

    protected Calendar calendar;

    private final Handler updateTimeHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        if (msg.what == MSG_UPDATE_TIME) {
          invalidate();
          if (shouldTimerBeRunning()) {
            sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
          }
        }
      }
    };

    private boolean isTimeZoneReceiverRegistered;
    private final BroadcastReceiver timeZoneReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        calendar.setTimeZone(TimeZone.getDefault());
        invalidate();
      }
    };

    @Override
    public void onCreate(SurfaceHolder holder) {
      super.onCreate(holder);

      setWatchFaceStyle(
          new WatchFaceStyle.Builder(BaseFaceService.this)
              .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
              .setAmbientPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
              .setShowUnreadCountIndicator(true)
              .setShowSystemUiTime(false)
              .setStatusBarGravity(Gravity.CENTER_VERTICAL)
              .setHotwordIndicatorGravity(Gravity.TOP)
              .setViewProtectionMode(
                  WatchFaceStyle.PROTECT_STATUS_BAR | WatchFaceStyle.PROTECT_HOTWORD_INDICATOR)
              .build()
      );

      calendar = Calendar.getInstance();
    }

    @Override
    public void onPropertiesChanged(Bundle properties) {
      super.onPropertiesChanged(properties);

      isLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
      hasBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
    }

    @Override
    public void onTimeTick() {
      super.onTimeTick();
      invalidate();
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
      super.onVisibilityChanged(visible);
      if (visible) {
        registerReceiver();
        calendar.setTimeZone(TimeZone.getDefault());
      } else {
        unregisterReceiver();
      }
      updateTimer();
    }

    protected void updateTimer() {
      updateTimeHandler.removeMessages(MSG_UPDATE_TIME);
      if (shouldTimerBeRunning()) {
        updateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
      }
    }

    private boolean shouldTimerBeRunning() {
      return !isInAmbientMode() && isVisible();
    }

    private void registerReceiver() {
      if (isTimeZoneReceiverRegistered) {
        return;
      }
      isTimeZoneReceiverRegistered = true;
      IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
      BaseFaceService.this.registerReceiver(timeZoneReceiver, filter);
    }

    private void unregisterReceiver() {
      if (!isTimeZoneReceiverRegistered) {
        return;
      }
      isTimeZoneReceiverRegistered = false;
      BaseFaceService.this.unregisterReceiver(timeZoneReceiver);
    }
  }
}

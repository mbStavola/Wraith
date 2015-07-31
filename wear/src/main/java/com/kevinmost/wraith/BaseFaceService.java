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
import com.kevinmost.wraith.event.Event;
import com.kevinmost.wraith.hand.WraithHand;
import com.kevinmost.wraith.task.LoadCalendarEventsTask;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public abstract class BaseFaceService extends CanvasWatchFaceService {
  protected static final int MSG_UPDATE_TIME = 0;
  protected static final int MSG_LOAD_CALENDAR_EVENTS = 1;

  public abstract class Engine extends CanvasWatchFaceService.Engine {
    protected boolean isLowBitAmbient;
    protected boolean hasBurnInProtection;

    protected Calendar calendar;

    private LoadCalendarEventsTask loadCalendarEventsTask;
    private boolean isTimeZoneReceiverRegistered;

    private final Handler updateHandler = new UpdateHandler();
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
              .setStatusBarGravity(Gravity.TOP)
              .setHotwordIndicatorGravity(Gravity.TOP)
              .setViewProtectionMode(
                  WatchFaceStyle.PROTECT_STATUS_BAR | WatchFaceStyle.PROTECT_HOTWORD_INDICATOR)
              .build()
      );

      calendar = Calendar.getInstance();
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      super.onSurfaceChanged(holder, format, width, height);
      // Cache these values when the dimensions change (shouldn't be often) instead of calculating
      // them every frame
      WatchParams.width = width;
      WatchParams.height = height;
      WatchParams.centerX = width / 2F;
      WatchParams.centerY = height / 2F;
    }

    @Override
    public void onPropertiesChanged(Bundle properties) {
      super.onPropertiesChanged(properties);

      isLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
      hasBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
    }

    @Override
    public void onAmbientModeChanged(boolean inAmbientMode) {
      super.onAmbientModeChanged(inAmbientMode);
      if (isLowBitAmbient) {
        final boolean antiAlias = !inAmbientMode;
        WraithHand.HOUR.getPaint().setAntiAlias(antiAlias);
        WraithHand.MINUTE.getPaint().setAntiAlias(antiAlias);
      }
      invalidate();
      updateTimer();
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
        updateHandler.sendEmptyMessage(MSG_LOAD_CALENDAR_EVENTS);
      } else {
        unregisterReceiver();
        updateHandler.removeMessages(MSG_LOAD_CALENDAR_EVENTS);
        loadCalendarEventsCancel();
      }
      updateTimer();
    }

    private void loadCalendarEventsCancel() {
      if (loadCalendarEventsTask != null) {
        loadCalendarEventsTask.cancel(true);
      }
    }

    private void updateTimer() {
      updateHandler.removeMessages(MSG_UPDATE_TIME);
      if (shouldTimerBeRunning()) {
        updateHandler.sendEmptyMessage(MSG_UPDATE_TIME);
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

    protected abstract void onCalendarEventsLoaded(List<Event> loadedEvents);

    private class UpdateHandler extends Handler {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case MSG_UPDATE_TIME:
            invalidate();
            if (shouldTimerBeRunning()) {
              sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
            }
            break;
          case MSG_LOAD_CALENDAR_EVENTS:
            loadCalendarEventsCancel();
            loadCalendarEventsTask = new LoadCalendarEventsTask(getApplicationContext()) {
              @Override
              protected List<Event> doInBackground(Void... voids) {
                final List<Event> events = super.doInBackground(voids);
                onCalendarEventsLoaded(events);
                return events;
              }

              @Override
              protected void onPostExecute(List<Event> events) {
                super.onPostExecute(events);
                if (isVisible()) {
                  updateHandler.sendEmptyMessageDelayed(MSG_LOAD_CALENDAR_EVENTS, 1000 * 60);
                }
              }
            };
            loadCalendarEventsTask.execute();
            break;
        }
      }
    }
  }
}

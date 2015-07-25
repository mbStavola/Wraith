package com.kevinmost.wraith.task;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.wearable.provider.WearableCalendarContract;
import android.text.format.DateUtils;
import com.kevinmost.wraith.event.Event;

import java.util.ArrayList;
import java.util.List;

public class LoadCalendarEventsTask extends AsyncTask<Void, Void, List<Event>> {

  private static final String[] DESIRED_CALENDAR_COLUMNS = {
      CalendarContract.Instances.BEGIN,
      CalendarContract.Instances.END,
      CalendarContract.Instances.DISPLAY_COLOR
  };

  private final ContentResolver contentResolver;

  public LoadCalendarEventsTask(Context context) {
    contentResolver = context.getContentResolver();
  }

  @Override
  protected List<Event> doInBackground(Void... voids) {
    final long begin = System.currentTimeMillis();
    final Uri.Builder builder = WearableCalendarContract.Instances.CONTENT_URI.buildUpon();
    ContentUris.appendId(builder, begin);
    ContentUris.appendId(builder, begin + DateUtils.DAY_IN_MILLIS / 2);
    final Cursor cursor = contentResolver.query(builder.build(),
        DESIRED_CALENDAR_COLUMNS,
        null,
        null,
        null);
    final List<Event> events = new ArrayList<>();
    while (cursor.moveToNext()) {
      final long start = cursor.getLong(0);
      final long end = cursor.getLong(1);
      final long color = cursor.getLong(2);
      events.add(new Event(start, end, color));
    }
    cursor.close();
    return events;
  }
}

package com.lenddo.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import com.lenddo.data.models.AndroidAttendee;
import com.lenddo.data.models.CalendarDetails;
import com.lenddo.data.models.CalendarEvent;
import com.lenddo.data.models.EventInstances;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarEventsManager {

    private static final String TAG = CalendarEventsManager.class.getName();
    Context context;
    static CalendarEventsManager instance;

    protected CalendarEventsManager(Context context) {
        this.context = context;
    }


    public static void init(Context context) {
        instance = new CalendarEventsManager(context);
    }

    public static CalendarEventsManager getInstance() {
        return instance;
    }

    public CalendarDetails getCalendar(long calendarId) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"),
                (new String[]{CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME}),
                CalendarContract.Calendars._ID + " = ?", new String[]{Long.toString(calendarId)}, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                CalendarDetails calendar = new CalendarDetails();
                calendar.setId(cursor.getLong(0));
                calendar.setName(cursor.getString(1));
                return calendar;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public ArrayList<EventInstances> getEventInstances(long eventId) {
        ArrayList<EventInstances> results = new ArrayList<EventInstances>();

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, Long.MIN_VALUE);
        ContentUris.appendId(builder, Long.MAX_VALUE);

        Cursor cursor = context.getContentResolver().query(builder.build(),
                (new String[]{CalendarContract.Instances._ID,
                        CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.END,
                        CalendarContract.Instances.END_DAY,
                        CalendarContract.Instances.END_MINUTE,
                        CalendarContract.Instances.START_DAY,
                        CalendarContract.Instances.START_MINUTE
                       }),
                CalendarContract.Instances.EVENT_ID + " = ?", new String[]{Long.toString(eventId)}, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    EventInstances instance = new EventInstances();
                    instance.setId(cursor.getLong(0));
                    instance.setBegin(cursor.getLong(1));
                    instance.setEnd(cursor.getLong(2));
                    instance.setEndDay(cursor.getLong(3));
                    instance.setEndMinute(cursor.getLong(4));
                    instance.setStartDay(cursor.getInt(5));
                    instance.setStartMinute(cursor.getInt(6));
                    results.add(instance);
                    cursor.moveToNext();
                }
            }
        } finally {
            cursor.close();
        }
        return results;
    }

    public ArrayList<CalendarEvent> readCalendarEvent() {
        ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[] {CalendarContract.Events._ID, CalendarContract.Events.TITLE,
                                CalendarContract.Events.DESCRIPTION,
                                CalendarContract.Events.DTSTART,
                                CalendarContract.Events.DTEND,
                                CalendarContract.Events.EVENT_LOCATION,
                                CalendarContract.Events.ALL_DAY,
                                CalendarContract.Events.CALENDAR_ID,
                                CalendarContract.Events.HAS_ATTENDEE_DATA,
                                CalendarContract.Events.ORGANIZER,
                                CalendarContract.Events.EVENT_TIMEZONE,
                                CalendarContract.Events.EVENT_END_TIMEZONE
                        }, null,
                        null, null);
        try {
            cursor.moveToFirst();
            // fetching calendars name
            Log.d(TAG, "Reading calendar events");
            for (int i = 0; i < cursor.getCount(); i++) {

                CalendarEvent event = new CalendarEvent();
                event.setId(cursor.getLong(0));
                event.setTitle(cursor.getString(1));
                event.setDescription(cursor.getString(2));
                event.setStartDate(getDate(cursor.getLong(3)));
                event.setEndDate(getDate(cursor.getLong(4)));
                event.setLocation(cursor.getString(5));
                event.setAllDay(cursor.getInt(6));
                long calendarId = cursor.getLong(7);
                CalendarDetails cal = getCalendar(calendarId);
                event.setCalendarDetails(cal);

                if (cursor.getInt(8) > 0) {
                    ArrayList <AndroidAttendee> androidAttendees = getAttendee(event.getId());
                    event.setAttendees(androidAttendees);
                }

                event.setOrganizer(cursor.getString(9));
                event.setTimezone(cursor.getString(10));
                event.setEndTimezone(cursor.getString(11));

                event.setEventInstances(getEventInstances(event.getId()));

                cursor.moveToNext();
                Log.d(TAG, "event " + event.getTitle());
                events.add(event);
            }
        } catch (android.database.sqlite.SQLiteException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return  events;
    }

    private ArrayList<AndroidAttendee> getAttendee(long id) {
        ArrayList<AndroidAttendee> results = new ArrayList<AndroidAttendee>();
        Cursor cursor = context.getContentResolver().query(CalendarContract.Attendees.CONTENT_URI,
                (new String[]{CalendarContract.Attendees._ID,
                CalendarContract.Attendees.ATTENDEE_EMAIL,
                CalendarContract.Attendees.ATTENDEE_NAME,
                CalendarContract.Attendees.ATTENDEE_STATUS,
                CalendarContract.Attendees.ATTENDEE_TYPE,
                CalendarContract.Attendees.ATTENDEE_RELATIONSHIP}),
                CalendarContract.Attendees.EVENT_ID + " = ?", new String[]{Long.toString(id)}, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0 ; i < cursor.getCount(); i++) {
                    AndroidAttendee attendee = new AndroidAttendee();
                    attendee.setId(cursor.getLong(0));
                    attendee.setEmail(cursor.getString(1));
                    attendee.setName(cursor.getString(2));
                    attendee.setStatus(getStatusString(cursor.getInt(3)));
                    attendee.setType(getTypeString(cursor.getInt(4)));
                    attendee.setRelationship(getRelationshipString(cursor.getInt(5)));
                    cursor.moveToNext();
                    results.add(attendee);
                }
            }
        } finally {
            cursor.close();
        }
        return results;
    }

    private String getTypeString(int type) {
        switch (type) {
            case CalendarContract.Attendees.TYPE_NONE:
                return "none";
            case CalendarContract.Attendees.TYPE_OPTIONAL:
                return "optional";
            case CalendarContract.Attendees.TYPE_REQUIRED:
                return "required";
            case CalendarContract.Attendees.TYPE_RESOURCE:
                return "resource";
            default:
                return "others";
        }
    }

    private String getRelationshipString(int status) {
        switch(status) {
            case CalendarContract.Attendees.RELATIONSHIP_ATTENDEE:
                return "attendee";
            case CalendarContract.Attendees.RELATIONSHIP_NONE:
                return "none";
            case CalendarContract.Attendees.RELATIONSHIP_ORGANIZER:
                return "organizer";
            case CalendarContract.Attendees.RELATIONSHIP_PERFORMER:
                return "performer";
            case CalendarContract.Attendees.RELATIONSHIP_SPEAKER:
                return "speaker";
            default:
                return "others";
        }
    }

    private String getStatusString(int status) {
        switch(status) {
            case CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED:
                return "accepted";
            case CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED:
                return "declined";
            case CalendarContract.Attendees.ATTENDEE_STATUS_NONE:
                return "none";
            case CalendarContract.Attendees.ATTENDEE_STATUS_INVITED:
                return "invited";
            case CalendarContract.Attendees.ATTENDEE_STATUS_TENTATIVE:
                return "tentative";
            default:
                return "others";
        }
    }

    public static Date getDate(long milliSeconds) {
        if (milliSeconds > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return calendar.getTime();
        } else {
            return null;
        }
    }
}

package com.lenddo.data.models;

import java.util.ArrayList;
import java.util.Date;

public class CalendarEvent implements Comparable<CalendarEvent> {

	private String title;
	private Date begin;
	private Date end;
	private int allDay;
    private String description;
    private Date startDate;
    private Date endDate;
    private String location;
    private long id;
    private CalendarDetails calendarDetails;
    private long isOrganizer;
    private long isPrimary;
    private ArrayList<AndroidAttendee> attendees;
    private String organizer;
    private String timezone;
    private String endTimezone;
    private ArrayList<EventInstances> eventInstances;

    public CalendarEvent() {

	}

	public CalendarEvent(String title, Date begin, Date end, int allDay) {
		setTitle(title);
		setBegin(begin);
		setEnd(end);
		setAllDay(allDay);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public int isAllDay() {
		return allDay;
	}

	public void setAllDay(int allDay) {
		this.allDay = allDay;
	}

	@Override
	public String toString() {
		return getTitle() + " " + getBegin() + " " + getEnd() + " " + isAllDay();
	}

	@Override
	public int compareTo(CalendarEvent other) {
		// -1 = less, 0 = equal, 1 = greater
		return getBegin().compareTo(other.begin);
	}

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setCalendarDetails(CalendarDetails calendarDetails) {
        this.calendarDetails = calendarDetails;
    }

    public CalendarDetails getCalendarDetails() {
        return calendarDetails;
    }

    public void setIsOrganizer(long isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    public long getIsOrganizer() {
        return isOrganizer;
    }

    public void setIsPrimary(long isPrimary) {
        this.isPrimary = isPrimary;
    }

    public long getIsPrimary() {
        return isPrimary;
    }

    public void setAttendees(ArrayList<AndroidAttendee> attendees) {
        this.attendees = attendees;
    }

    public ArrayList<AndroidAttendee> getAttendees() {
        return attendees;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setEndTimezone(String endTimezone) {
        this.endTimezone = endTimezone;
    }

    public String getEndTimezone() {
        return endTimezone;
    }

    public void setEventInstances(ArrayList<EventInstances> eventInstances) {
        this.eventInstances = eventInstances;
    }

    public ArrayList<EventInstances> getEventInstances() {
        return eventInstances;
    }
}
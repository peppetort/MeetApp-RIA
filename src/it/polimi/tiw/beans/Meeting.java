package it.polimi.tiw.beans;

import java.sql.Date;
import java.sql.Time;

public class Meeting {
	private final int meetingId;
	private final int userId;
	private final String title;
	private final Date meetingDate;
	private final Time startTime;
	private final Time endTime;
	private final int maxParticipants;

	public Meeting(int meetingId, int userId, String title, Date meetingDate, Time startTime, Time endTime, int maxParticipants){
		this.meetingId = meetingId;
		this.userId = userId;
		this.title = title;
		this.meetingDate = meetingDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.maxParticipants = maxParticipants;
	}

	public Meeting(int userId, String title, Date meetingDate, Time startTime, Time endTime, int maxParticipants){
		this.meetingId = -1;
		this.userId = userId;
		this.title = title;
		this.meetingDate = meetingDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.maxParticipants = maxParticipants;
	}

	public int getMeetingId() {
		return meetingId;
	}

	public int getUserId(){
		return this.userId;
	}

	public String getTitle() {
		return title;
	}

	public Date getMeetingDate() {
		return meetingDate;
	}

	public Time getStartTime() {
		return startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public int getMaxParticipants(){
		return maxParticipants;
	}

}

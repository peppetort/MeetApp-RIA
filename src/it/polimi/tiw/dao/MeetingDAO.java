package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Meeting;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MeetingDAO {
	private final Connection connection;
	private final Date actualDate = new Date(new java.util.Date().getTime());
	private final Time actualTime = java.sql.Time.valueOf(LocalTime.now());

	public MeetingDAO(Connection connection) {
		this.connection = connection;
	}

	public Meeting checkExistence(String title) throws SQLException {
		Meeting meeting = null;

		String query = "SELECT * FROM Meeting WHERE Title=?";

		PreparedStatement pstatement;
		ResultSet result;

		pstatement = connection.prepareStatement(query);
		pstatement.setString(1, title);
		result = pstatement.executeQuery();

		if (result.next()) {
			int meetingId = result.getInt("MeetingId");
			int userId = result.getInt("CreatorId");
			Date meetingDate = result.getDate("MeetingDate");
			Time startTime = result.getTime("StartTime");
			Time endTime = result.getTime("EndTime");
			int maxParticipants = result.getInt("MaxParticipants");

			meeting = new Meeting(meetingId, userId, title, meetingDate, startTime, endTime, maxParticipants);
		}

		return meeting;
	}

	public List<Meeting> getMeetingsByUser(int userId) throws SQLException {
		ArrayList<Meeting> userMeetings = new ArrayList<>();
		Meeting meeting;

		String query = "SELECT * FROM Meeting WHERE (CreatorId=? AND (MeetingDate>? OR (MeetingDate=? AND EndTime>?))) " +
				"ORDER BY MeetingDate, StartTime ASC";


		PreparedStatement pstatement;
		ResultSet result;

		pstatement = connection.prepareStatement(query);
		pstatement.setInt(1, userId);
		pstatement.setDate(2, actualDate);
		pstatement.setDate(3, actualDate);
		pstatement.setTime(4, actualTime);
		result = pstatement.executeQuery();

		while (result.next()) {
			int meetingId = result.getInt("MeetingId");
			String title = result.getString("Title");
			Date meetingDate = result.getDate("MeetingDate");
			Time startTime = result.getTime("StartTime");
			Time endTime = result.getTime("EndTime");
			int maxParticipants = result.getInt("MaxParticipants");

			meeting = new Meeting(meetingId, userId, title, meetingDate, startTime, endTime, maxParticipants);
			userMeetings.add(meeting);
		}

		return userMeetings;
	}

	public void createMeeting(Meeting meeting) throws SQLException {
		String query = "INSERT INTO Meeting (CreatorId, Title, MeetingDate, StartTime, EndTime, MaxParticipants) VALUES(?, ?, ?, ?, ?, ?)";

		int userId = meeting.getUserId();
		String title = meeting.getTitle();
		Date meetingDate = meeting.getMeetingDate();
		Time startTime = meeting.getStartTime();
		Time endTime = meeting.getEndTime();
		int maxParticipants = meeting.getMaxParticipants();

		PreparedStatement pstatment = connection.prepareStatement(query);
		pstatment.setInt(1, userId);
		pstatment.setString(2, title);
		pstatment.setDate(3, meetingDate);
		pstatment.setTime(4, startTime);
		pstatment.setTime(5, endTime);
		pstatment.setInt(6, maxParticipants);
		pstatment.executeUpdate();
	}
}

package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Meeting;


import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ParticipationDAO {
	private final Connection connection;
	private final Date actualDate = new Date(new java.util.Date().getTime());
	private final Time actualTime = java.sql.Time.valueOf(LocalTime.now());

	public ParticipationDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Meeting> getMeetingsByUser(int userId) throws SQLException {
		ArrayList<Meeting> allMeetings = new ArrayList<>();
		Meeting meeting;

		String meetingIdQuery = "SELECT MeetingId FROM Participation WHERE UserId=?";
		String meetingQuery = "SELECT * FROM Meeting WHERE (MeetingId=? AND (MeetingDate>? OR (MeetingDate=? AND EndTime>?)))" +
				"ORDER BY MeetingDate, StartTime ASC";


		PreparedStatement meetingIdPStatement;
		PreparedStatement meetingPStatement;
		ResultSet meetingIdResult;
		ResultSet meetingResult;

		meetingIdPStatement = connection.prepareStatement(meetingIdQuery);
		meetingIdPStatement.setInt(1, userId);
		meetingIdResult = meetingIdPStatement.executeQuery();

		while (meetingIdResult.next()) {
			int meetingId = meetingIdResult.getInt("MeetingId");

			meetingPStatement = connection.prepareStatement(meetingQuery);
			meetingPStatement.setInt(1, meetingId);
			meetingPStatement.setDate(2, actualDate);
			meetingPStatement.setDate(3, actualDate);
			meetingPStatement.setTime(4, actualTime);
			meetingResult = meetingPStatement.executeQuery();

			while (meetingResult.next()) {

				String title = meetingResult.getString("Title");
				Date meetingDate = meetingResult.getDate("MeetingDate");
				Time startTime = meetingResult.getTime("StartTime");
				Time endTime = meetingResult.getTime("EndTime");
				int maxParticipants = meetingResult.getInt("MaxParticipants");

				meeting = new Meeting(meetingId, userId, title, meetingDate, startTime, endTime, maxParticipants);
				allMeetings.add(meeting);
			}
		}

		return allMeetings;
	}

	public void addParticipants(Meeting meeting, List<Integer> users) throws SQLException {
		String query = "INSERT INTO Participation (MeetingId, UserId) VALUES(?, ?)";

		PreparedStatement pstatment = connection.prepareStatement(query);
		pstatment.setInt(1, meeting.getMeetingId());

		for (int user: users){
			pstatment.setInt(2, user);
			pstatment.executeUpdate();
		}
	}
}

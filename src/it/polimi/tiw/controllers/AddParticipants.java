package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.dao.MeetingDAO;
import it.polimi.tiw.dao.ParticipationDAO;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/AddParticipants")
@MultipartConfig
public class AddParticipants extends HttpServletChecker {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();

		Meeting meeting = (Meeting) session.getAttribute("meeting");
		String[] checkedIds = request.getParameterValues("checked");

		MeetingDAO meetingDAO = new MeetingDAO(connection);
		ParticipationDAO participationDAO = new ParticipationDAO(connection);

		try {
			List<Integer> selected = new ArrayList<>();

			if (checkedIds != null) {
				for (String sId : checkedIds) {
					sId = StringEscapeUtils.escapeJava(sId);

					if(isWrong(sId)){
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Invalid ids");
						return;
					}

					selected.add(Integer.parseInt(sId));
				}
			}

			if (selected.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("You must select at least one participant");
			} else if (selected.size() > meeting.getMaxParticipants()) {

				int difference = selected.size() - meeting.getMaxParticipants();

				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Remove at least " + difference + "participants");
			} else {
				meetingDAO.createMeeting(meeting);
				meeting = meetingDAO.checkExistence(meeting.getTitle());
				participationDAO.addParticipants(meeting, selected);
				session.removeAttribute("meeting");
				response.setStatus(HttpServletResponse.SC_OK);
			}

		}catch (NumberFormatException e1){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid ids");
		} catch (SQLException e2) {
			e2.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error");
		}
	}
}

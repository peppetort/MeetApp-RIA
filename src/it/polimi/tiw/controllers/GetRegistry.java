package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GetRegistry")
public class GetRegistry extends HttpServletChecker {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);

		int userId = ((User) session.getAttribute("user")).getId();

		UserDAO userDAO = new UserDAO(connection);

		List<User> usersList;

		try {
			usersList = userDAO.getAllUsersBut(userId);

			if(usersList.isEmpty()){
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().println("You are the only user");
			}else {
				Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
				String json = gson.toJson(usersList);

				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
			}

		}catch (SQLException e2){
			e2.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}
}

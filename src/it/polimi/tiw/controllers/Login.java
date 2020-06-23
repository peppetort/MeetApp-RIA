package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/Login")
@MultipartConfig
public class Login extends HttpServletChecker {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));

		try{
			if(isWrong(username) || isWrong(password)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Invalid credentials");
				return;
			}

			UserDAO userDAO = new UserDAO(connection);
			User user = userDAO.checkExistence(username);

			if(user == null){
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Unknown user");
				return;
			}

			if(!user.getPassword().equals(password)){
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Wrong password");
				return;
			}

			HttpSession session = request.getSession();
			session.setAttribute("user", user);

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(user.getName() + " " + user.getSurname());

		}catch (SQLException e){
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error");
		}

	}
}

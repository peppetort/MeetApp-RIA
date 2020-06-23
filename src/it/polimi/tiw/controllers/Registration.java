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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/Registration")
@MultipartConfig
public class Registration extends HttpServletChecker {
	public final Pattern VALID_EMAIL_ADDRESS_REGEX =
			Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		String password1 = StringEscapeUtils.escapeJava(request.getParameter("password1"));
		String password2 = StringEscapeUtils.escapeJava(request.getParameter("password2"));
		String name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		String surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		
		try {
			if(isWrong(username) || isWrong(password1) || isWrong(password2) || isWrong(name) || isWrong(surname)
					|| isWrong(email)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Invalid credentials");
				return;
			}

			if(!password1.equals(password2)){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Passwords do not match");
				return;
			}

			Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);

			if(!matcher.find()){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Invalid email address");
				return;
			}

			UserDAO userDAO = new UserDAO(connection);
			User user = userDAO.checkExistence(username);

			if(user != null){
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				response.getWriter().println("User " + username + " already exist");
				return;
			}

			user = new User(username, name, surname, email, password1);
			userDAO.insertUser(user);
			user = userDAO.checkExistence(username);

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

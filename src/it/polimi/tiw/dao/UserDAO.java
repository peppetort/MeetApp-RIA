package it.polimi.tiw.dao;

import it.polimi.tiw.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
	private final Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public User checkExistence(String username) throws SQLException {
		User user = null;

		String query = "SELECT * FROM User WHERE Username=?";

		PreparedStatement pstatement;
		ResultSet result;

		pstatement = connection.prepareStatement(query);
		pstatement.setString(1, username);
		result = pstatement.executeQuery();

		if (result.next()) {
			int id = result.getInt("UserId");
			String name = result.getString("Name");
			String surname = result.getString("Surname");
			String email = result.getString("Email");
			String password = result.getString("Password");

			user = new User(id, username, name, surname, email, password);
		}

		return user;
	}

	public void insertUser(User user) throws SQLException {
		String query = "INSERT INTO User (Username, Name, Surname, Password, Email) VALUES(?, ?, ?, ?, ?)";
		String username = user.getUsername();
		String name = user.getName();
		String surname = user.getSurname();
		String email = user.getEmail();
		String password = user.getPassword();

		PreparedStatement pstatment = connection.prepareStatement(query);
		pstatment.setString(1, username);
		pstatment.setString(2, name);
		pstatment.setString(3, surname);
		pstatment.setString(4, password);
		pstatment.setString(5, email);
		pstatment.executeUpdate();
	}

	public List<User> getAllUsersBut(int id) throws SQLException {
		ArrayList<User> allUsers = new ArrayList<>();
		User user;

		String query = "SELECT * FROM User WHERE UserId<>?";

		PreparedStatement pstatement;
		ResultSet result;

		pstatement = connection.prepareStatement(query);
		pstatement.setInt(1, id);
		result = pstatement.executeQuery();

		while (result.next()) {
			int userId = result.getInt("UserId");
			String username = result.getString("Username");
			String name = result.getString("Name");
			String surname = result.getString("Surname");
			String email = result.getString("Email");
			String password = "";

			user = new User(userId, username, name, surname, email, password);
			allUsers.add(user);
		}

		return allUsers;
	}
}

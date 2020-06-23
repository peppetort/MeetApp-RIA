package it.polimi.tiw.beans;

public class User {
	private final int id;
	private final String username;
	private final String name;
	private final String surname;
	private final String email;
	private final String password;

	public User(int id, String username, String name, String surname, String email, String password){
		this.id = id;
		this.username = username;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
	}

	public User(String username, String name, String surname, String email,  String password){
		this.id = -1;
		this.username = username;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
	}

	public int getId(){
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getName(){
		return this.name;
	}

	public String getSurname(){
		return this.surname;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}
}

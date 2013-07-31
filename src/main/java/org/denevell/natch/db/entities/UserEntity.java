package org.denevell.natch.db.entities;


public class UserEntity {
	
	public static final String NAMED_QUERY_FIND_WITH_USERNAME_AND_PASSWORD = "findWithUsernamePassword";
	public static final String NAMED_QUERY_FIND_EXISTING_USERNAME= "findExistingUsername";
	public static final String NAMED_QUERY_PARAM_PASSWORD = "password";
	public static final String NAMED_QUERY_PARAM_USERNAME = "username";
	
	private String username;
	private String password;
	
	// For testing only
	public UserEntity(String username, String pass) {
		this.username = username;
		this.password = pass;
	}
	
	public UserEntity() {
	}
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}

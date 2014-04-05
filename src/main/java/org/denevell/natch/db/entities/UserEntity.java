package org.denevell.natch.db.entities;

import javax.persistence.Transient;

import org.denevell.natch.io.users.RegisterResourceInput;
import org.denevell.natch.utils.PasswordSaltUtils;

public class UserEntity {
	
	public static final String NAMED_QUERY_FIND_WITH_USERNAME_AND_PASSWORD = "findWithUsernamePassword";
	public static final String NAMED_QUERY_FIND_EXISTING_USERNAME= "findExistingUsername";
	public static final String NAMED_QUERY_COUNT= "countUsers";
	public static final String NAMED_QUERY_LIST_USERS= "listUsers";
	public static final String NAMED_QUERY_PARAM_PASSWORD = "password";
	public static final String NAMED_QUERY_PARAM_USERNAME = "username";
	
	private String username;
	private String password;
	@Transient
	private String originalPassword;
	private boolean admin;
	private boolean passwordResetRequest;
	
	// For testing only
	public UserEntity(String username, String pass) {
		this.username = username;
		this.password = pass;
	}
	
	public UserEntity(RegisterResourceInput register) {
		this.username = register.getUsername();
		this.originalPassword = register.getPassword();
		generatePassword(originalPassword);
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

	public void generatePassword(String pass) {
		this.password = new PasswordSaltUtils().generatedSaltedPassword(pass);
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Transient
	public String getOriginalPassword() {
		return originalPassword;
	}

	@Transient
	public void setOriginalPassword(String originalPassword) {
		this.originalPassword = originalPassword;
	}

	public boolean isPasswordResetRequest() {
		return passwordResetRequest;
	}

	public void setPasswordResetRequest(boolean passwordResetRequest) {
		this.passwordResetRequest = passwordResetRequest;
	}

}

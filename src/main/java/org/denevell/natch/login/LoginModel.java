package org.denevell.natch.login;

import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.utils.PasswordSaltUtils;

public class LoginModel {
	
	private UserEntityQueries mUserEntityQueries;
	public enum LoginResult { LOGGED_IN, USER_INPUT_ERROR, UNKNOWN_ERROR, CREDENTIALS_INCORRECT };
	/**
	 * For DI testing
	 */
	public LoginModel(UserEntityQueries ueq) {
		mUserEntityQueries = ueq;
	}
	
	public LoginModel() {
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
	}

	public LoginResult login(String username, String password) {
		if(password==null || password.trim().length()==0 || username==null || username.trim().length()==0) {
			return LoginResult.USER_INPUT_ERROR;
		}
		try {
			boolean res = mUserEntityQueries.areCredentialsCorrect(username, password);
			if(res) {
				return LoginResult.LOGGED_IN;
			} else {
				return LoginResult.CREDENTIALS_INCORRECT;
			}
		} catch(Exception e) {
			// TODO: Log
			e.printStackTrace();
			return LoginResult.UNKNOWN_ERROR;
		} 
	}	

}

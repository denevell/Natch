package org.denevell.natch.login;

import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.utils.PasswordSaltUtils;

public class LoginModel {
	
	private UserEntityQueries mUserEntityQueries;
	private LoginAuthDataSingleton mAuthDataGenerator;
	public enum LoginEnumResult { LOGGED_IN, USER_INPUT_ERROR, UNKNOWN_ERROR, CREDENTIALS_INCORRECT };
	public static class LoginResult {
		private String authKey = "";
		private LoginEnumResult result;
		public LoginResult(LoginEnumResult result) {
			this.result = result;
		}
		public LoginResult(LoginEnumResult result, String authKey) {
			this.result = result;
			this.authKey = authKey;
		}
		public String getAuthKey() {
			return authKey;
		}
		public void setAuthKey(String authKey) {
			this.authKey = authKey;
		}
		public LoginEnumResult getResult() {
			return result;
		}
		public void setResult(LoginEnumResult result) {
			this.result = result;
		}
	}
	
	/**
	 * For DI testing
	 * @param authKeyGenerator 
	 */
	public LoginModel(UserEntityQueries ueq, LoginAuthDataSingleton authKeyGenerator) {
		mUserEntityQueries = ueq;
		mAuthDataGenerator = authKeyGenerator;
	}
	
	public LoginModel() {
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
		mAuthDataGenerator = LoginAuthDataSingleton.getInstance();
	}

	public LoginResult login(String username, String password) {
		if(password==null || password.trim().length()==0 || username==null || username.trim().length()==0) {
			return new LoginResult(LoginEnumResult.USER_INPUT_ERROR);
		}
		try {
			boolean res = mUserEntityQueries.areCredentialsCorrect(username, password);
			if(res) {
				String authKey = mAuthDataGenerator.generate(username);
				return new LoginResult(LoginEnumResult.LOGGED_IN, authKey);
			} else {
				return new LoginResult(LoginEnumResult.CREDENTIALS_INCORRECT);
			}
		} catch(Exception e) {
			// TODO: Log
			e.printStackTrace();
			return new LoginResult(LoginEnumResult.UNKNOWN_ERROR);
		} 
	}	

}


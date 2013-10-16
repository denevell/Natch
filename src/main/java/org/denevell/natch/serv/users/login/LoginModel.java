package org.denevell.natch.serv.users.login;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.PasswordSaltUtils;

public class LoginModel {
	
	private UserEntityQueries mUserEntityQueries;
	private LoginAuthKeysSingleton mAuthDataGenerator;
	private EntityManager mEntityManager;
	public static String LOGGED_IN = "loggedIn";
	public static String USER_INPUT_ERROR = "inputError";
	public static String UNKNOWN_ERROR = "unknownError";
	public static String CREDENTIALS_INCORRECT = "credIncorect";
	public static class LoginResult {
		private String authKey = "";
		private String result;
		public LoginResult(String result) {
			this.result = result;
		}
		public LoginResult(String result, String authKey) {
			this.result = result;
			this.authKey = authKey;
		}
		public String getAuthKey() {
			return authKey;
		}
		public void setAuthKey(String authKey) {
			this.authKey = authKey;
		}
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
	}
	
	/**
	 * For DI testing
	 */
	public LoginModel(UserEntityQueries ueq, LoginAuthKeysSingleton authKeyGenerator, EntityManagerFactory factory, EntityManager entityManager) {
		mUserEntityQueries = ueq;
		mAuthDataGenerator = authKeyGenerator;
		mEntityManager =  entityManager;
		mUserEntityQueries = ueq;
	}
	
	public LoginModel() {
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
		mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
	}
	
	public void init() {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager();   		
	}

	public void close() {
		EntityUtils.closeEntityConnection(mEntityManager);
	}		
	
	public LoginResult login(String username, String password) {
		try {
			if(password==null || password.trim().length()==0 || username==null || username.trim().length()==0) {
				return new LoginResult(USER_INPUT_ERROR);
			}
			UserEntity res = mUserEntityQueries.areCredentialsCorrect(username, password, mEntityManager);
			if(res!=null) {
				String authKey = mAuthDataGenerator.generate(res);
				return new LoginResult(LOGGED_IN, authKey);
			} else {
				return new LoginResult(CREDENTIALS_INCORRECT);
			}
		} catch(Exception e) {
			Log.info(this.getClass(), e.toString());
			e.printStackTrace();
			return new LoginResult(UNKNOWN_ERROR);
		} 
	}
	
	

}
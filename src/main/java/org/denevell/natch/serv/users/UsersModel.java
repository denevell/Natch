package org.denevell.natch.serv.users;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.PasswordSaltUtils;

public class UsersModel {
	
	private UserEntityQueries mUserEntityQueries;
	private LoginAuthKeysSingleton mAuthDataGenerator;
	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;
	private PasswordSaltUtils mPasswordSalter;
	public static String LOGGED_IN = "loggedIn";
	public static String USER_INPUT_ERROR = "inputError";
	public static String UNKNOWN_ERROR = "unknownError";
	public static String CREDENTIALS_INCORRECT = "credIncorect";
	public static String REGISTERED="registered";
	public static String DUPLICATE_USERNAME="dupusername";
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
	public UsersModel(UserEntityQueries ueq, LoginAuthKeysSingleton authKeyGenerator, EntityManagerFactory factory, EntityManager entityManager, PasswordSaltUtils saltUtils) {
		mUserEntityQueries = ueq;
		mAuthDataGenerator = authKeyGenerator;
		mFactory = factory;
		mEntityManager =  entityManager;
		mUserEntityQueries = ueq;
		mPasswordSalter = saltUtils;
	}
	
	public UsersModel() {
		mFactory = JPAFactoryContextListener.sFactory;
		mEntityManager = mFactory.createEntityManager();   		
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
		mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
		mPasswordSalter = new PasswordSaltUtils();
	}
	
	public String addUserToSystem(String username, String password) {
		EntityTransaction trans = null;
		try {
			if(password==null || password.trim().length()==0 || username==null || username.trim().length()==0) {
				return USER_INPUT_ERROR;
			}
			UserEntity u = new UserEntity();
			password = mPasswordSalter.generatedSaltedPassword(password);
			u.setPassword(password);
			u.setUsername(username);
			if(!mUserEntityQueries.doesUsernameExist(username, mEntityManager)) {
				trans = mEntityManager.getTransaction();
				trans.begin();
				mEntityManager.persist(u);
				trans.commit();
				return REGISTERED;
			} else {
				return DUPLICATE_USERNAME;
			}
		} catch(Exception e) {
			Log.info(this.getClass(), e.toString());
			e.printStackTrace();
			if(trans!=null && trans.isActive()) trans.rollback();
			return UNKNOWN_ERROR;
		} 
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
	
	public boolean logout(String authKey) {
		if(authKey==null || authKey.trim().length()==0) {
			return false;
		}
		mAuthDataGenerator.remove(authKey);
		UserEntity username = mAuthDataGenerator.retrieveUserEntity(authKey);
		if(username!=null) {
			return false;
		} else {
			return true;
		}
	}	

	/**
	 * @return Null when false
	 */
	public UserEntity loggedInAs(String authKey) {
		UserEntity username = mAuthDataGenerator.retrieveUserEntity(authKey);
		return username;
	}

	public void close() {
		EntityUtils.closeEntityConnection(mEntityManager);
	}	

}
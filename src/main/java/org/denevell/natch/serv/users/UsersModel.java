package org.denevell.natch.serv.users;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.PersistenceInfo;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.PasswordSaltUtils;

public class UsersModel {
	
	private UserEntityQueries mUserEntityQueries;
	private LoginAuthKeysSingleton mAuthDataGenerator;
	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;
	private PasswordSaltUtils mPasswordSalter;
	public enum LoginEnumResult { LOGGED_IN, USER_INPUT_ERROR, UNKNOWN_ERROR, CREDENTIALS_INCORRECT };
	public enum RegisterResult { REGISTERED, UNKNOWN_ERROR, DUPLICATE_USERNAME, USER_INPUT_ERROR }
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
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
		mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
		mFactory = Persistence.createEntityManagerFactory(PersistenceInfo.EntityManagerFactoryName);
		mEntityManager = mFactory.createEntityManager();		
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
		mPasswordSalter = new PasswordSaltUtils();
	}
	
	
	public RegisterResult addUserToSystem(String username, String password) {
		if(password==null || password.trim().length()==0 || username==null || username.trim().length()==0) {
			return RegisterResult.USER_INPUT_ERROR;
		}
		UserEntity u = new UserEntity();
		password = mPasswordSalter.generatedSaltedPassword(password);
		u.setPassword(password);
		u.setUsername(username);
		EntityTransaction trans = null;
		try {
			trans = mEntityManager.getTransaction();
			if(!mUserEntityQueries.doesUsernameExist(username)) {
				trans.begin();
				mEntityManager.persist(u);
				trans.commit();
				return RegisterResult.REGISTERED;
			} else {
				return RegisterResult.DUPLICATE_USERNAME;
			}
		} catch(Exception e) {
			Log.info(this.getClass(), e.toString());
			e.printStackTrace();
			if(trans!=null && trans.isActive()) trans.rollback();
			return RegisterResult.UNKNOWN_ERROR;
		} finally {
			EntityUtils.closeEntityConnection(mFactory, mEntityManager);
		}
	}	

	public LoginResult login(String username, String password) {
		if(password==null || password.trim().length()==0 || username==null || username.trim().length()==0) {
			return new LoginResult(LoginEnumResult.USER_INPUT_ERROR);
		}
		try {
			UserEntity res = mUserEntityQueries.areCredentialsCorrect(username, password);
			if(res!=null) {
				String authKey = mAuthDataGenerator.generate(res);
				return new LoginResult(LoginEnumResult.LOGGED_IN, authKey);
			} else {
				return new LoginResult(LoginEnumResult.CREDENTIALS_INCORRECT);
			}
		} catch(Exception e) {
			Log.info(this.getClass(), e.toString());
			e.printStackTrace();
			return new LoginResult(LoginEnumResult.UNKNOWN_ERROR);
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

}
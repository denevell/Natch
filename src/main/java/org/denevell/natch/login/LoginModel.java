package org.denevell.natch.login;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.utils.PasswordSaltUtils;

public class LoginModel {
	
	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;
	private UserEntityQueries mUserEntityQueries;
	public enum LoginResult { LOGGED_IN, USER_INPUT_ERROR, UNKNOWN_ERROR, CREDENTIALS_INCORRECT };
	/**
	 * For DI testing
	 */
	public LoginModel(EntityManager entityManager, EntityManagerFactory factory, UserEntityQueries ueq) {
		mFactory = factory;
		mEntityManager =  entityManager;
		mUserEntityQueries = ueq;
	}
	
	public LoginModel() {
		mFactory = Persistence.createEntityManagerFactory("users");
		mEntityManager = mFactory.createEntityManager();		
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
	}
	
	private void closeEntityConnection() {
		try {
			mEntityManager.close();
			mFactory.close();
		} catch(Exception e) {
			// Log
		}
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

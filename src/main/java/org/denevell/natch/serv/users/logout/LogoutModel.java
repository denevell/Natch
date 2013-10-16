package org.denevell.natch.serv.users.logout;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;

public class LogoutModel {
	
	private LoginAuthKeysSingleton mAuthDataGenerator;
	private EntityManager mEntityManager;
	public static String LOGGED_IN = "loggedIn";
	public static String USER_INPUT_ERROR = "inputError";
	public static String UNKNOWN_ERROR = "unknownError";
	public static String CREDENTIALS_INCORRECT = "credIncorect";
		
	/**
	 * For DI testing
	 */
	public LogoutModel(UserEntityQueries ueq, LoginAuthKeysSingleton authKeyGenerator, EntityManagerFactory factory, EntityManager entityManager) {
		mAuthDataGenerator = authKeyGenerator;
		mEntityManager =  entityManager;
	}
	
	public LogoutModel() {
		mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
	}
	
	public void init() {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager();   		
	}

	public void close() {
		EntityUtils.closeEntityConnection(mEntityManager);
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


}
package org.denevell.natch.serv.users.status;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;

public class StatusModel {
	
	private LoginAuthKeysSingleton mAuthDataGenerator;
	private EntityManager mEntityManager;
	public static String LOGGED_IN = "loggedIn";
	public static String USER_INPUT_ERROR = "inputError";
	public static String UNKNOWN_ERROR = "unknownError";
	public static String CREDENTIALS_INCORRECT = "credIncorect";
		
	/**
	 * For DI testing
	 */
	public StatusModel(LoginAuthKeysSingleton authKeyGenerator, EntityManager entityManager) {
		mAuthDataGenerator = authKeyGenerator;
		mEntityManager =  entityManager;
	}
	
	public StatusModel() {
		mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
	}
	
	public void init() {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager();   		
	}

	public void close() {
		EntityUtils.closeEntityConnection(mEntityManager);
	}		
	
	/**
	 * @return Null when false
	 */
	public UserEntity loggedInAs(String authKey) {
		UserEntity username = mAuthDataGenerator.retrieveUserEntity(authKey);
		return username;
	}


}
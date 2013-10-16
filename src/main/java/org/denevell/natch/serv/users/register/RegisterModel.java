package org.denevell.natch.serv.users.register;

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

public class RegisterModel {
	
	private UserEntityQueries mUserEntityQueries;
	private EntityManager mEntityManager;
	private PasswordSaltUtils mPasswordSalter;
	public static String USER_INPUT_ERROR = "inputError";
	public static String UNKNOWN_ERROR = "unknownError";
	public static String REGISTERED="registered";
	public static String DUPLICATE_USERNAME="dupusername";
	
	/**
	 * For DI testing
	 */
	public RegisterModel(UserEntityQueries ueq, LoginAuthKeysSingleton authKeyGenerator, EntityManagerFactory factory, EntityManager entityManager, PasswordSaltUtils saltUtils) {
		mUserEntityQueries = ueq;
		mEntityManager =  entityManager;
		mPasswordSalter = saltUtils;
	}
	
	public RegisterModel() {
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
		mPasswordSalter = new PasswordSaltUtils();
	}

	public void init() {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager();   		
	}

	public void close() {
		EntityUtils.closeEntityConnection(mEntityManager);
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


}
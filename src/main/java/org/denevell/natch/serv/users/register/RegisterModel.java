package org.denevell.natch.serv.users.register;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.PasswordSaltUtils;

public class RegisterModel {
	
	private EntityManager mEntityManager;
	private PasswordSaltUtils mPasswordSalter;
	public static String USER_INPUT_ERROR = "inputError";
	public static String UNKNOWN_ERROR = "unknownError";
	public static String REGISTERED="registered";
	public static String DUPLICATE_USERNAME="dupusername";
	private UserEntityQueries mUserEntityQueries;
	
	/**
	 * For DI testing
	 */
	public RegisterModel(LoginAuthKeysSingleton authKeyGenerator, 
	        EntityManager entityManager, 
	        PasswordSaltUtils saltUtils,
	        UserEntityQueries entityQueries) {
		mEntityManager =  entityManager;
		mPasswordSalter = saltUtils;
		mUserEntityQueries = entityQueries;
	}
	
	public RegisterModel() {
		mPasswordSalter = new PasswordSaltUtils();
		mUserEntityQueries = new UserEntityQueries(null);
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
			if(isFirstUser()) u.setAdmin(true);
			if(!doesUsernameExist(username, mEntityManager)) {
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

	public boolean doesUsernameExist(String username, EntityManager entityManager) {
		List<UserEntity> resultList = mUserEntityQueries.getUserByUsername(username, entityManager);
		boolean okay = false;
		if(resultList!=null) okay = resultList.size()>0;
		return okay;
	}	
	
	public boolean isFirstUser() {
		Query q = mEntityManager.createNamedQuery(UserEntity.NAMED_QUERY_COUNT); 
	    Number cResults=(Number) q.getSingleResult();		
		return cResults.intValue()==0;	
	}
	

}
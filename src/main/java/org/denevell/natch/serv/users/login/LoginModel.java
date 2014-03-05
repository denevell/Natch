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
	
	public static class UserEntityAuthKey {
		public UserEntity ue;
		public String authKey;
		public UserEntityAuthKey(UserEntity ue, String authKey) {
			this.ue = ue;
			this.authKey = authKey;
		}
	}
	
	public UserEntityAuthKey login(String username, String password) {
		try {
			UserEntity res = mUserEntityQueries.areCredentialsCorrect(username, password, mEntityManager);
			if(res!=null) {
				String authKey = mAuthDataGenerator.generate(res);
				return new UserEntityAuthKey(res, authKey);
			} else {
				return null;
			}
		} catch(Exception e) {
			Log.info(this.getClass(), e.toString());
			e.printStackTrace();
			throw new RuntimeException(e); 
		} 
	}
	
	

}
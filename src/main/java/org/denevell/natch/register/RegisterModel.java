package org.denevell.natch.register;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.utils.PasswordSaltUtils;

public class RegisterModel {
	
	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;
	private UserEntityQueries mUserEntityQueries;
	private PasswordSaltUtils mPasswordSalter;
	public enum RegisterResult {
		REGISTERED, UNKNOWN_ERROR, DUPLICATE_USERNAME, USER_INPUT_ERROR
	}

	/**
	 * For DI testing
	 */
	public RegisterModel(EntityManager entityManager, EntityManagerFactory factory, UserEntityQueries ueq, PasswordSaltUtils saltUtils) {
		mFactory = factory;
		mEntityManager =  entityManager;
		mUserEntityQueries = ueq;
		mPasswordSalter = saltUtils;
	}
	
	public RegisterModel() {
		mFactory = Persistence.createEntityManagerFactory("users");
		mEntityManager = mFactory.createEntityManager();		
		mUserEntityQueries = new UserEntityQueries(new PasswordSaltUtils());
		mPasswordSalter = new PasswordSaltUtils();
	}
	
	private void closeEntityConnection() {
		try {
			mEntityManager.close();
			mFactory.close();
		} catch(Exception e) {
			// Log
		}
	}	
	
	public void clearTestDb() {
		EntityTransaction trans = mEntityManager.getTransaction();
		trans.begin();
		Query q = mEntityManager.createQuery("delete from UserEntity");
		q.executeUpdate();
		trans.commit();
		closeEntityConnection();
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
			// TODO: Log
			e.printStackTrace();
			if(trans!=null && trans.isActive()) trans.rollback();
			closeEntityConnection();		
			return RegisterResult.UNKNOWN_ERROR;
		} finally {
			closeEntityConnection();		
		}
	}

}

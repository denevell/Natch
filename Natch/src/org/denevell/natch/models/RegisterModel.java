package org.denevell.natch.models;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;

public class RegisterModel {
	
	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;
	private UserEntityQueries mUserEntityQueries;

	/**
	 * For DI testing
	 */
	public RegisterModel(EntityManager entityManager, EntityManagerFactory factory, UserEntityQueries ueq ) {
		mFactory = factory;
		mEntityManager =  entityManager;
		mUserEntityQueries = ueq;
	}
	
	public RegisterModel() {
		mFactory = Persistence.createEntityManagerFactory("users");
		mEntityManager = mFactory.createEntityManager();		
		mUserEntityQueries = new UserEntityQueries();
	}
	
	public enum RegisterResult {
		REGISTERED, UNKNOWN_ERROR, DUPLICATE_USERNAME, USER_INPUT_ERROR
	}
	
	private void closeEntityConnection() {
		try {
			mEntityManager.close();
			mFactory.close();
		} catch(Exception e) {
			// Log
		}
	}	
	
	public RegisterResult addUserToSystem(String username, String password) {
		if(password==null || password.trim().length()==0 || username==null || username.trim().length()==0) {
			return RegisterResult.USER_INPUT_ERROR;
		}
		UserEntity u = new UserEntity();
		u.setPassword(password);
		u.setUsername(username);
		EntityTransaction trans = null;
		try {
			mEntityManager.lock(this, LockModeType.PESSIMISTIC_WRITE);
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
			if(trans!=null) trans.rollback();
			closeEntityConnection();		
			return RegisterResult.UNKNOWN_ERROR;
		} finally {
			closeEntityConnection();		
		}
	}
}

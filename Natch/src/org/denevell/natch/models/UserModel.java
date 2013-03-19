package org.denevell.natch.models;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;

public class UserModel {
	
	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;
	private UserEntityQueries mUserEntityQueries;

	/**
	 * For DI testing
	 */
	public UserModel(EntityManager entityManager, EntityManagerFactory factory, UserEntityQueries ueq ) {
		mFactory = factory;
		mEntityManager =  entityManager;
		mUserEntityQueries = ueq;
	}
	
	public UserModel() {
		mFactory = Persistence.createEntityManagerFactory("users");
		mEntityManager = mFactory.createEntityManager();		
		mUserEntityQueries = new UserEntityQueries();
	}
	
	private void closeEntityConnection() {
		try {
			mEntityManager.close();
			mFactory.close();
		} catch(Exception e) {
			// Log
		}
	}	
	
	public boolean addUserToSystem(String password, String username) {
		if(password==null || password.trim().length()==0 || username==null || username.trim().length()==0) {
			return false;
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
				return true;		
			} else {
				return false;
			}
		} catch(Exception e) {
			// TODO: Log
			e.printStackTrace();
			if(trans!=null) trans.rollback();
			closeEntityConnection();		
			return false;
		} finally {
			closeEntityConnection();		
		}
	}
}

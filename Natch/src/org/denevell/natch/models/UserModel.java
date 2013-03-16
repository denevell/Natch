package org.denevell.natch.models;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.UserEntity;

public class UserModel {
	
	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;

	/**
	 * For DI
	 */
	public UserModel(EntityManager entityManager, EntityManagerFactory factory ) {
		mFactory = factory;
		mEntityManager =  entityManager;
	}
	
	public UserModel() {
		mFactory = Persistence.createEntityManagerFactory("users");
		mEntityManager = mFactory.createEntityManager();		
	}
	
	private void closeEntityConnection() {
		try {
			mEntityManager.close();
			mFactory.close();
		} catch(Exception e) {
			// Log
		}
	}	
	
	public boolean checkUsernameAndPassword(String username, String password) {
		TypedQuery<UserEntity> q = mEntityManager 
				.createNamedQuery(UserEntity.NAMED_QUERY_FIND_WITH_USERNAME_AND_PASSWORD, UserEntity.class)
				.setParameter(UserEntity.NAMED_QUERY_PARAM_USERNAME, username)
				.setParameter(UserEntity.NAMED_QUERY_PARAM_PASSWORD, password);
		List<UserEntity> resultList = q.getResultList();
		boolean okay = false;
		if(resultList!=null) okay = resultList.size()>0;
		closeEntityConnection();		
		return okay;
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
			trans.begin();
			mEntityManager.persist(u);
			trans.commit();
			closeEntityConnection();		
			return true;		
		} catch(Exception e) {
			// TODO: Log
			e.printStackTrace();
			if(trans!=null) trans.rollback();
			closeEntityConnection();		
			return false;
		}
	}
}

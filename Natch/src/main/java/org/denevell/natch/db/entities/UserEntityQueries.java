package org.denevell.natch.db.entities;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class UserEntityQueries {
	
	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;

	public UserEntityQueries() {
		mFactory = Persistence.createEntityManagerFactory("users");
		mEntityManager = mFactory.createEntityManager();		
	}	

	public boolean doesUsernameExist(String username) {
		TypedQuery<UserEntity> q = mEntityManager.createNamedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME, UserEntity.class)
			.setParameter(UserEntity.NAMED_QUERY_PARAM_USERNAME, username);
		List<UserEntity> resultList = q.getResultList();
		boolean okay = false;
		if(resultList!=null) okay = resultList.size()>0;
		closeEntityConnection();		
		return okay;
	}	
	
	private void closeEntityConnection() {
		try {
			mEntityManager.close();
			mFactory.close();
		} catch(Exception e) {
			// Log
		}
	}		
	
}

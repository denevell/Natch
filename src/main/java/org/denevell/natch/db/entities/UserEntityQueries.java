package org.denevell.natch.db.entities;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.denevell.natch.utils.PasswordSaltUtils;

public class UserEntityQueries {
	
	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;
	private PasswordSaltUtils mSaltedPasswordUtils;

	public UserEntityQueries(PasswordSaltUtils saltUtils) {
		mFactory = Persistence.createEntityManagerFactory("users");
		mEntityManager = mFactory.createEntityManager();		
		mSaltedPasswordUtils = new PasswordSaltUtils();
	}	
	
	private void closeEntityConnection() {
		try {
			mEntityManager.close();
			mFactory.close();
		} catch(Exception e) {
			// Log
		}
	}

	private List<UserEntity> getUserByUsername(String username) {
		TypedQuery<UserEntity> q = mEntityManager.createNamedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME, UserEntity.class)
			.setParameter(UserEntity.NAMED_QUERY_PARAM_USERNAME, username);
		List<UserEntity> resultList = q.getResultList();
		closeEntityConnection();		
		return resultList;
	}	

	public boolean doesUsernameExist(String username) {
		List<UserEntity> resultList = getUserByUsername(username);
		boolean okay = false;
		if(resultList!=null) okay = resultList.size()>0;
		return okay;
	}

	public boolean areCredentialsCorrect(String username, String password) {
		List<UserEntity> results = getUserByUsername(username);
		if(results==null || results.size()<=0) {
			return false;
		} else {
			UserEntity user = results.get(0);
			String storedPassword = user.getPassword();
			if(storedPassword==null) {
				return false;
			} else {
				boolean correctPw = mSaltedPasswordUtils.checkSaltedPassword(password, storedPassword);
				return correctPw;
			}
		}
	}

	
}

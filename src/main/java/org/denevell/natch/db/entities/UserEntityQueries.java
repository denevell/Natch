package org.denevell.natch.db.entities;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.denevell.natch.utils.PasswordSaltUtils;

public class UserEntityQueries {
	
	private PasswordSaltUtils mSaltedPasswordUtils;

	public UserEntityQueries(PasswordSaltUtils saltUtils) {
		mSaltedPasswordUtils = new PasswordSaltUtils();
	}	

	public static List<UserEntity> getUserByUsername(String username, EntityManager entityManager) {
		TypedQuery<UserEntity> q = entityManager.createNamedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME, UserEntity.class)
			.setParameter(UserEntity.NAMED_QUERY_PARAM_USERNAME, username);
		List<UserEntity> resultList = q.getResultList();
		return resultList;
	}	


	public UserEntity areCredentialsCorrect(String username, String password, EntityManager entityManager) {
		List<UserEntity> results = getUserByUsername(username, entityManager);
		if(results==null || results.size()<=0) {
			return null;
		} else {
			UserEntity user = results.get(0);
			String storedPassword = user.getPassword();
			if(storedPassword==null) {
				return null;
			} else {
				boolean correctPw = mSaltedPasswordUtils.checkSaltedPassword(password, storedPassword);
				if(correctPw) {
					return user;
				} else {
					return null;
				}
			}
		}
	}

	
}

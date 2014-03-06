package org.denevell.natch.db.entities;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class UserEntityQueries {
	

	public UserEntityQueries() {
	}	

	public List<UserEntity> getUserByUsername(String username, EntityManager entityManager) {
		TypedQuery<UserEntity> q = entityManager.createNamedQuery(UserEntity.NAMED_QUERY_FIND_EXISTING_USERNAME, UserEntity.class)
			.setParameter(UserEntity.NAMED_QUERY_PARAM_USERNAME, username);
		List<UserEntity> resultList = q.getResultList();
		return resultList;
	}	

}

package org.denevell.natch.serv.users.list;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;

public class UsersListModel {
	
	private EntityManager mEntityManager;
	
	/**
	 * For DI testing
	 */
	public UsersListModel(EntityManager entityManager) {
		mEntityManager =  entityManager;
	}
	
	public UsersListModel() {
	}
	
	public void init() {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager();   		
	}

	public void close() {
		EntityUtils.closeEntityConnection(mEntityManager);
	}		
	
	public List<UserEntity> listUsers() {
	    TypedQuery<UserEntity> nq = mEntityManager.createNamedQuery(UserEntity.NAMED_QUERY_LIST_USERS, UserEntity.class);
	    return nq.getResultList();
	}
	
	

}
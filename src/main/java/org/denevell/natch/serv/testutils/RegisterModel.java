package org.denevell.natch.serv.testutils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.denevell.natch.db.entities.PersistenceInfo;
import org.denevell.natch.utils.EntityUtils;

public class RegisterModel {
	
	private EntityManagerFactory mFactory;
	private EntityManager mEntityManager;

	public RegisterModel() {
		mFactory = Persistence.createEntityManagerFactory(PersistenceInfo.EntityManagerFactoryName);
		mEntityManager = mFactory.createEntityManager();		
	}
	
	public void clearTestDb() {
		EntityTransaction trans = mEntityManager.getTransaction();
		trans.begin();
		Query q = mEntityManager.createQuery("delete from ThreadEntity");
		q.executeUpdate();
		q = mEntityManager.createQuery("delete from PostEntity");
		q.executeUpdate();
		q = mEntityManager.createQuery("delete from UserEntity");
		q.executeUpdate();
		trans.commit();
		EntityUtils.closeEntityConnection(mFactory, mEntityManager);
	}

}

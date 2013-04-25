package org.denevell.natch.serv.testutils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.denevell.natch.db.entities.PersistenceInfo;
import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
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
		List<PostEntity> result = mEntityManager.createQuery("select a from PostEntity a", PostEntity.class).getResultList();
		for (PostEntity postEntity : result) {
			mEntityManager.remove(postEntity);
		}
		List<ThreadEntity> resultT = mEntityManager.createQuery("select a from ThreadEntity a", ThreadEntity.class).getResultList();
		for (ThreadEntity postEntity : resultT) {
			mEntityManager.remove(postEntity);
		}
		List<UserEntity> resultU = mEntityManager.createQuery("select a from UserEntity a", UserEntity.class).getResultList();
		for (UserEntity postEntity : resultU) {
			mEntityManager.remove(postEntity);
		}
		trans.commit();
		EntityUtils.closeEntityConnection(mFactory, mEntityManager);
	}

}

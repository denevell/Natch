package org.denevell.natch.serv.testutils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.ThreadEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;

public class TestUtilsModel {
	
	private EntityManager mEntityManager;
	private EntityManagerFactory mFactory;

	public TestUtilsModel() {
		mFactory = JPAFactoryContextListener.sFactory;
	}
	
	public void clearTestDb() {
		if(JPAFactoryContextListener.sTestDb == false) {
			throw new RuntimeException("Computer says no.");
		} else {
			try {
				mEntityManager = mFactory.createEntityManager(); 		
				EntityTransaction trans = mEntityManager.getTransaction();
				trans.begin();
				List<ThreadEntity> resultT = mEntityManager.createQuery("select a from ThreadEntity a", ThreadEntity.class).getResultList();
				for (ThreadEntity postEntity : resultT) {
					mEntityManager.remove(postEntity);
				}
				List<PostEntity> result = mEntityManager.createQuery("select a from PostEntity a", PostEntity.class).getResultList();
				for (PostEntity postEntity : result) {
					mEntityManager.remove(postEntity);
				}
				List<UserEntity> resultU = mEntityManager.createQuery("select a from UserEntity a", UserEntity.class).getResultList();
				for (UserEntity postEntity : resultU) {
					mEntityManager.remove(postEntity);
				}
				trans.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				EntityUtils.closeEntityConnection(mEntityManager);
			}
		}
	}

}

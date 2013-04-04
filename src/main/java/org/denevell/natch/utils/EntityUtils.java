package org.denevell.natch.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityUtils {
	
	public static void closeEntityConnection(EntityManagerFactory factory, EntityManager entityManager) {
		try {
			entityManager.close();
			factory.close();
		} catch(Exception e) {
			Log.info(EntityUtils.class, e.toString());
		}
	}		

}

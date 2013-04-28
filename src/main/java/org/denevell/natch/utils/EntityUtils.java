package org.denevell.natch.utils;

import javax.persistence.EntityManager;

public class EntityUtils {
	
	public static void closeEntityConnection(EntityManager entityManager) {
		try {
			if(entityManager!=null) entityManager.close();
		} catch(Exception e) {
			e.printStackTrace();
			Log.info(EntityUtils.class, e.toString());
		}
	}		

}

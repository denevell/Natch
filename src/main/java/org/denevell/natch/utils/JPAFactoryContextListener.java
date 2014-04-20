package org.denevell.natch.utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class JPAFactoryContextListener implements ServletContextListener{
	public static EntityManagerFactory sFactory;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("JPA context listener destroyed");
		sFactory.close();
	}
 
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("JPA context listener started");
		sFactory = Persistence.createEntityManagerFactory("PERSISTENCE_UNIT_NAME");		
	}

}
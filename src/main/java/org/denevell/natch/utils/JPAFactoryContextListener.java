package org.denevell.natch.utils;

import java.io.IOException;
import java.util.jar.Attributes;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.denevell.natch.model.entities.PersistenceInfo;

public class JPAFactoryContextListener implements ServletContextListener{
	public static EntityManagerFactory sFactory;
	public static boolean sIsRunningAgainstTestDb = false;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("JPA context listener destroyed");
		sFactory.close();
	}
 
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("JPA context listener started");
		try {
			Attributes attr = ManifestUtils.getManifest(arg0.getServletContext());
			String isProd = attr.getValue("ISPROD");
			if(isProd !=null && isProd.equals("TRUE")) {
				Log.info(getClass(), "Using production database.");
				sIsRunningAgainstTestDb = false;
				sFactory = Persistence.createEntityManagerFactory(PersistenceInfo.ProdEntityManagerFactoryName);		
			} else {
				Log.info(getClass(), "Using test database.");
				sIsRunningAgainstTestDb = true;
				sFactory = Persistence.createEntityManagerFactory(PersistenceInfo.TestEntityManagerFactoryName);		
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't find manifest.mf. Running from Eclipse?");
		}
	}

}
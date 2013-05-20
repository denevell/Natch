package org.denevell.natch.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.denevell.natch.db.entities.PersistenceInfo;

public class JPAFactoryContextListener implements ServletContextListener{
	public static EntityManagerFactory sFactory;
	public static boolean sTestDb = false;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("JPA context listener destroyed");
		sFactory.close();
	}
 
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("JPA context listener started");
		try {
			Attributes attr = getManifest(arg0.getServletContext());
			String isProd = attr.getValue("ISPROD");
			if(isProd !=null && isProd.equals("TRUE")) {
				Log.info(getClass(), "Using production database.");
				sTestDb = false;
				sFactory = Persistence.createEntityManagerFactory(PersistenceInfo.ProdEntityManagerFactoryName);		
			} else {
				Log.info(getClass(), "Using test database.");
				sTestDb = true;
				sFactory = Persistence.createEntityManagerFactory(PersistenceInfo.TestEntityManagerFactoryName);		
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't find manifest.mf. Running from Eclipse?");
		}
	}

	private Attributes getManifest(ServletContext ctx) throws IOException {
		InputStream inputStream = ctx.getResourceAsStream("/META-INF/MANIFEST.MF");
		Manifest manifest = new Manifest(inputStream);	
		return manifest.getMainAttributes();
	}
}
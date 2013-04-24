package org.denevell.natch.auth;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

 
public class LoginClearContextListener implements ServletContextListener{
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener destroyed");
		LoginAuthKeysSingleton instance = LoginAuthKeysSingleton.getInstance();
		instance.kill();
		//unload drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println("deregistering jdbc driver: " + driver);
            } catch (SQLException e) {
                System.out.println("Error deregistering driver " +  driver + " : " + e);
            }
        }		
	}
 
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("ServletContextListener started");	
	}
}
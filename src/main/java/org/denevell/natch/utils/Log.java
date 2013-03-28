package org.denevell.natch.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.denevell.natch.serv.login.LoginModel;

public class Log {
	
	static {
	    try {
		    Reader isr = new InputStreamReader(LoginModel.class.getClassLoader().getResourceAsStream("log4j.properties"));
		    Properties p = new Properties();
			p.load(isr);
			PropertyConfigurator.configure(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void info(@SuppressWarnings("rawtypes") Class c, String s) {
		Logger.getLogger(c).info(s);
	}
}

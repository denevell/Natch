package org.denevell.natch.utils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyApplication {

	public static void main(String[] args) {
	       Server server = new Server(8085);         
	       
	       HandlerCollection hc = new HandlerCollection();

	        WebAppContext webContext = new WebAppContext();
	        webContext.setContextPath("/rest");
	        webContext.setServer(server);
	        webContext.setWar("/home/user/workspace/Natch-Jsp/Natch-REST/Natch-REST-ForAutomatedTests.war");
	        
	        hc.addHandler(webContext);

	        WebAppContext webContext1 = new WebAppContext();
	        webContext1.setContextPath("/jsp/");
	        webContext1.setServer(server);
	        webContext1.setWar("/home/user/workspace/Natch-Jsp/Natch-Jsp-ForAutomatedTests.war");

	        hc.addHandler(webContext1);

	        server.setHandler(hc);
	    try {
			server.start();
			server.join();		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

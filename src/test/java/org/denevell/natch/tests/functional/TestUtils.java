package org.denevell.natch.tests.functional;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class TestUtils {

	public static WebResource getRESTClient() {
		String baseUrl = "http://localhost:8080/Natch-Functional/";
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl);		
	}

	public static WebResource getRESTRegisterClient() {
		String baseUrl = "http://localhost:8080/Natch-Functional/";
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl).path("rest").path("user");		
	}

}

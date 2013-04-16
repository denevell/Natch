package org.denevell.natch.tests.functional;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class TestUtils {

	public static void deleteAllDbs() {
	    String baseUrl = "http://localhost:8080/Natch-Functional/";
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    WebResource res = client.resource(baseUrl);
            res.path("rest").path("testutils").delete();
	}

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

	public static WebResource getLoginClient() {
		String baseUrl = "http://localhost:8080/Natch-Functional/";
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl).path("rest").path("user").path("login");
	}

	public static WebResource getRegisterClient() {
		String baseUrl = "http://localhost:8080/Natch-Functional/";
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl).path("rest").path("user");		
	}

	public static WebResource getAddPostClient() {
		String baseUrl = "http://localhost:8080/Natch-Functional/";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("add");		
	}

	public static WebResource getDeletePostClient() {
		String baseUrl = "http://localhost:8080/Natch-Functional/";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("del");		
	}

	public static WebResource getEditPostClient() {
		String baseUrl = "http://localhost:8080/Natch-Functional/";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("del");		
	}

	public static WebResource getListPostsClient() {
		String baseUrl = "http://localhost:8080/Natch-Functional/";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post");		
	}

	public static WebResource getListPostThreadsPostClient() {
		String baseUrl = "http://localhost:8080/Natch-Functional/";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("threads");		
	}

}

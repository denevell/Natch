package org.denevell.natch.tests.functional;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class TestUtils {
	
	private static String getHost() {
		return "http://denevell.org:8080";
	}

	public static void deleteAllDbs() {
	    String baseUrl = getHost() + "/Natch-Functional/";
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    WebResource res = client.resource(baseUrl);
            res.path("rest").path("testutils").delete();
	}

	public static WebResource getRESTClient() {
		String baseUrl = getHost() + "/Natch-Functional/";
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl);		
	}

	public static WebResource getRESTRegisterClient() {
		String baseUrl = getHost() + "/Natch-Functional/";
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl).path("rest").path("user");		
	}

	public static WebResource getLoginClient() {
		String baseUrl = getHost() + "/Natch-Functional/";
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl).path("rest").path("user").path("login");
	}

	public static WebResource getRegisterClient() {
		String baseUrl = getHost() + "/Natch-Functional/";
	    ClientConfig config = new DefaultClientConfig();
	    Client client = Client.create(config);
	    return client.resource(baseUrl).path("rest").path("user");		
	}

	public static WebResource getAddPostClient() {
		String baseUrl = getHost() + "/Natch-Functional/";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("add");		
	}

	public static WebResource getDeletePostClient() {
		String baseUrl = getHost() + "/Natch-Functional/";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("del");		
	}

	public static WebResource getEditPostClient() {
		String baseUrl = getHost() + "/Natch-Functional/";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("edit");		
	}

	public static WebResource getListPostsClient() {
		String baseUrl = getHost() + "/Natch-Functional/";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post");		
	}

	public static WebResource getListPostThreadsPostClient() {
		String baseUrl = getHost() + "/Natch-Functional/";
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		return client.resource(baseUrl).path("rest").path("post").path("threads");		
	}

}

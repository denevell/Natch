package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.PostEntity.Output;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class PostSinglePO {
	
	private WebTarget mService;

	public PostSinglePO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_REST_SERVICE);
	}

	public Output single(long postId) {
    return mService
		.path("rest")
		  .path("single")
		  .path("PostEntity")
		  .path(String.valueOf(postId))
		  .queryParam("isLong", "true")
		  .request()
		  .accept(MediaType.APPLICATION_JSON)
		  .get(new GenericType<PostEntity.Output>() {});
	}	
	
	public PostSinglePO gives404OnBadId() {
	  try {
	    mService
	    .path("rest").path("post_single").path("blllarrr").request()
	    .accept(MediaType.APPLICATION_JSON)
	    .get(Output.class);
      org.junit.Assert.assertTrue("Expected a 404", false);
    } catch (WebApplicationException e) {
      org.junit.Assert.assertTrue("Expected a 404", 404==e.getResponse().getStatus());
    } 
    return this;
	}

}

package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.model.ThreadEntity.Output;
import org.denevell.natch.model.ThreadEntity.OutputList;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class ThreadsListPO {
	
	private WebTarget mService;

	public ThreadsListPO() {
		Client client = JerseyClientBuilder.createClient();
		client.register(JacksonFeature.class);
		mService = client.target(TestUtils.URL_REST_SERVICE);
	}

	public OutputList list(
	    int start, 
	    int limit) {
    return mService
		.path("rest").path("thread").path(String.valueOf(start)).path(String.valueOf(limit))
		.request()
		.accept(MediaType.APPLICATION_JSON)
		.get(OutputList.class);
	}

  public Output byThread(String threadId, int start, int limit) {
    return mService
		.path("rest").path("thread")
		.path(threadId).path(String.valueOf(start)).path(String.valueOf(limit))
		.request()
		.accept(MediaType.APPLICATION_JSON)
		.get(Output.class);
  }

  public ThreadsListPO byThreadShow404(String threadId, int start, int limit) {
    try {
      mService.path("rest").path("thread")
          .path(threadId).path(String.valueOf(start)).path(String.valueOf(limit)).request()
          .accept(MediaType.APPLICATION_JSON).get(Output.class);
      org.junit.Assert.assertFalse("Excepted 404", true);
      return null;
    } catch (WebApplicationException e) {
      org.junit.Assert.assertEquals(404, e.getResponse().getStatus());
    }
    return this;
  }

}

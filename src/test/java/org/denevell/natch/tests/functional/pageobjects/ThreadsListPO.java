package org.denevell.natch.tests.functional.pageobjects;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.entities.ThreadEntity.Output;
import org.denevell.natch.gen.ServList.OutputWithCount;
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

  public OutputWithCount<ThreadEntity.Output> list(
	    int start, 
	    int limit) {
    return mService
		.path("rest").path("list").path("ThreadEntity")
		  .queryParam("orderby", "latestPost.modified")
		  .queryParam("desc", "true")
		  .queryParam("count", "true")
		  .queryParam("start", start)
		  .queryParam("limit", limit).request()
		.accept(MediaType.APPLICATION_JSON)
		.get(new GenericType<OutputWithCount<ThreadEntity.Output>>() {});
	}

  public Output byThread(String threadId, int start, int limit) {
    return mService
		.path("rest")
		  .path("single_pagination")
		  .path("ThreadEntity")
		  .queryParam("idString", threadId)
		  .queryParam("pageField", "posts")
		  .queryParam("pageEntity", "PostEntity")
		  .queryParam("pageOrderby", "created")
		  .queryParam("pageDesc", "false")
		  .queryParam("pageStart", String.valueOf(start))
		  .queryParam("pageLimit", String.valueOf(limit))
		  .queryParam("whereField", "threadId")
		  .queryParam("whereValue", threadId)
		  .request()
		  .accept(MediaType.APPLICATION_JSON)
		  .get(new GenericType<ThreadEntity.Output>() {});
  }

  public OutputWithCount<ThreadEntity.Output> byTag(String tag, int start, int limit) {
    return mService
		.path("rest").path("list").path("ThreadEntity")
		  .queryParam("orderby", "latestPost.modified")
		  .queryParam("desc", "true")
		  .queryParam("count", "true")
		  .queryParam("member", tag)
		  .queryParam("memberof", "rootPost.tags")
		  .queryParam("start", start)
		  .queryParam("limit", limit).request()
		.accept(MediaType.APPLICATION_JSON)
		.get(new GenericType<OutputWithCount<ThreadEntity.Output>>() {});
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

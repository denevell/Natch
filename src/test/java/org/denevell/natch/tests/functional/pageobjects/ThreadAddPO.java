package org.denevell.natch.tests.functional.pageobjects;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.denevell.natch.entities.ThreadEntity.AddInput;
import org.denevell.natch.entities.ThreadEntity.Utils;
import org.denevell.natch.tests.functional.TestUtils;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public class ThreadAddPO {

  private WebTarget mService;

  public ThreadAddPO() {
    Client client = JerseyClientBuilder.createClient();
    client.register(JacksonFeature.class);
    mService = client.target(TestUtils.URL_REST_SERVICE);
  }

  public Response add(String subject, String content, String authKey) {
    return add(subject, content, null, null, authKey);
  }

  public Response add(String subject, String content, String threadId, String authKey) {
    return add(subject, content, threadId, null, authKey);
  }

  public Response add(String subject, String content, List<String> tags, String authKey) {
    return add(subject, content, null, tags, authKey);
  }

  public Response add(String subject, String content, String threadId, List<String> tags, String authKey) {
    AddInput input = new AddInput();
    input.content = content;
    input.subject = subject;
    if (threadId != null) {
      input.threadId = threadId;
    }
    if (tags != null) {
      input.tags = Utils.StringWrapper.fromStrings(tags);
    }
    return mService.path("rest").path("add").path("ThreadEntity%24AddInput").queryParam("push", "thread").request()
        .accept(MediaType.APPLICATION_JSON).header("AuthKey", authKey).put(Entity.json(input));
  }

}

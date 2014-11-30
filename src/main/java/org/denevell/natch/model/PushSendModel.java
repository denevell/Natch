package org.denevell.natch.model;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.denevell.jrappy.Jrappy;
import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.PushEntity;
import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.ManifestVars;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class PushSendModel {

  public static void sendPushNotifications(final ThreadEntity thread) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        String key = ManifestVars.getGCMKey();
        if (key == null || key.trim().length() == 0) {
          Logger.getLogger(getClass()).error("GCM KEY is null or blank");
        }
        Sender sender = new Sender(key);
        Jrappy<PushEntity> jrappy = new Jrappy<PushEntity>(
            JPAFactoryContextListener.sFactory);
        List<PushEntity> list = null;
        list = jrappy
              .namedQuery(PushEntity.NAMED_QUERY_LIST_IDS)
              .list(PushEntity.class);
        if(list!=null) {
          for (PushEntity pushEntity : list) {
            try {
              String registrationId = pushEntity.clientId;
              String s = new ObjectMapper().writeValueAsString(new CutDownThreadResource(thread));
              Message message = new Message.Builder().addData("thread", s) .build();
              Result result = sender.send(message, registrationId, 5);
              Logger.getLogger(getClass()).info("Push send result: " + result);
            } catch (Exception e) {
              Logger.getLogger(getClass()).error("Error sending push message: " + e.getMessage());
              e.printStackTrace();
            }
          }
          
        }
      }
    }).start();
  }

  public static class CutDownThreadResource {

    public List<String> tags;
    public String id;
    public String subject;
    public String author;
    public long numPosts;
    public long creation;
    public long modification;
    public long rootPostId;

    public CutDownThreadResource() {}
    
    public CutDownThreadResource(ThreadEntity tr) {
      subject = StringEscapeUtils.escapeHtml4(tr.rootPost.subject);
      author = tr.rootPost.username;
      numPosts = tr.numPosts;
      tags = PostEntity.Utils.getTagsEscaped(tr.rootPost.tags);
      rootPostId = tr.rootPost.id;
      modification = tr.rootPost.modified;
      creation = tr.rootPost.created;
      id = tr.id;
    }

  }

}

package org.denevell.natch.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.denevell.natch.utils.Adapter.AdapterEdit;
import org.denevell.natch.utils.ModelResponse.ModelExternaliser;
import org.denevell.natch.utils.UserGetLoggedInService.SystemUser;
import org.denevell.natch.utils.UserGetLoggedInService.Username;
import org.hibernate.validator.constraints.NotBlank;

public class PostEntity implements ModelExternaliser, Username {
	
  public static final int MAX_TAG_LENGTH = 20;
  public static final int MAX_SUBJECT_LENGTH = 300;
	
	public long id;
	public long created;
	public long modified;
	public String subject;
	public String content;
	public String threadId;
	public List<String> tags;
	public String username;
  public boolean adminEdited = false;
	
	public PostEntity() {}
	
	public PostEntity(PostEntity entity) {
		this.id = entity.id;
		this.created = entity.created;
		this.modified = entity.modified;
		this.subject = entity.subject;
		this.content = entity.content;
		this.threadId = entity.threadId;
		this.tags = entity.tags;
		this.username = entity.username;
		this.adminEdited = entity.adminEdited;
	}

  @Override
  public String getUsername() {
    return username;
  }

  public static class AddInput implements AdapterEdit<ThreadEntity> {
    @NotBlank(message = "Post must have content")
    public String content;
    @NotBlank(message = "Post must include thread id")
    public String threadId;

    public PostEntity adapt(String username) {
      PostEntity entity = new PostEntity();
      long created = new Date().getTime();
      entity.content = content;
      entity.threadId = threadId;
      entity.username = username;
      entity.created = created;
      entity.modified = created;
      return entity;
    }

    @Override
    public ThreadEntity updateEntity(ThreadEntity threadEntity, SystemUser user) {
      PostEntity pe = adapt(user.getUsername());
      pe.subject = threadEntity.rootPost.subject;
      threadEntity.latestPost = pe;
      threadEntity.posts.add(pe);
      threadEntity.numPosts = threadEntity.numPosts + 1;
      return threadEntity;
    }
  }

  public static class EditInput implements AdapterEdit<PostEntity> {
    @NotBlank(message = "Post must have content")
    public String content;

    @Override
    public PostEntity updateEntity(PostEntity postEntity, SystemUser user) {
      postEntity.content = content;
      postEntity.modified = new Date().getTime();
      if (!user.getUsername().equals(postEntity.username) && user.getAdmin()) postEntity.adminEdited = true;
      return postEntity;
    }
  }

  public static class Output {
    public long id;
    public String username;
    public String subject;
    public String content;
    public String threadId;
    public long creation;
    public long modification;
    public List<String> tags = new ArrayList<>();
    public boolean adminEdited;
  }
  
  @Override
  public Output toOutput() {
    Output output = new Output(); 
    output.username = this.username;
    output.creation = this.created;
    output.modification = this.modified;
    output.subject = StringEscapeUtils.escapeHtml4(this.subject);
    output.content = StringEscapeUtils.escapeHtml4(this.content);
    output.tags = Utils.getTagsEscaped(this.tags);
    output.adminEdited = this.adminEdited;
    output.id = this.id;
    output.threadId = this.threadId;
    return output;
  }

  public static class Utils {
    public static String createNewThreadId(String threadId, String subject) {
      if ((threadId == null || threadId.trim().length() == 0)) {
        long created = new Date().getTime();
        try {
          MessageDigest md5Algor = MessageDigest.getInstance("MD5");
          StringBuffer sb = new StringBuffer();
          if (subject == null || subject.isEmpty()) {
            subject = "-";
          }
          byte[] digest = md5Algor.digest(subject.getBytes());
          for (byte b : digest) {
            sb.append(Integer.toHexString((int) (b & 0xff)));
          }
          threadId = sb.toString();
        } catch (NoSuchAlgorithmException e) {
          Logger.getLogger(Utils.class).info("Couldn't get an MD5 hash. I guess we'll just use hashCode() then.");
          e.printStackTrace();
          threadId = String.valueOf(subject.hashCode());
        }
        threadId = threadId + String.valueOf(created);
      }
      return threadId;
    }
    public static List<String> getTagsEscaped(List<String> tags) {
      if (tags == null)
        return null;
      ArrayList<String> ts = new ArrayList<String>(); 
      for (int i = 0; i < tags.size(); i++) {
        String string = StringEscapeUtils.escapeHtml4(tags.get(i));
        ts.add(string);
      }
      return ts;
    }
  }

  @SuppressWarnings("serial")
  public static class OutputList extends ArrayList<PostEntity.Output> {
  }


}

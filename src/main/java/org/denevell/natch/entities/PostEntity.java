package org.denevell.natch.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.denevell.natch.utils.ModelResponse.ModelExternaliser;
import org.hibernate.validator.constraints.NotBlank;

public class PostEntity implements ModelExternaliser {
	
	public static final String NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE = "findByModData";
	public static final String NAMED_QUERY_FIND_BY_THREADID = "findByThreadId";
	public static final String NAMED_QUERY_PARAM_ID= "id";
	public static final String NAMED_QUERY_PARAM_THREADID = "threadId";
	public static final String NAMED_QUERY_FIND_BY_ID = "findById";
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

  public static class AddInput {
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
  }

  public static class EditInput {
    @NotBlank(message = "Post must have content")
    public String content;

    public PostEntity adapt() {
      PostEntity entity = new PostEntity();
      entity.content = content;
      entity.subject = ("-");
      return entity;
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

  public static class OutputList {
    public List<Output> posts = new ArrayList<Output>();
    public OutputList() {}

    public OutputList(List<PostEntity> entities) {
      List<Output> outputs = new ArrayList<Output>();
      for (PostEntity p : entities) {
        Output entity = p.toOutput();
        outputs.add(entity);
      }
      posts = outputs;
    }
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

}

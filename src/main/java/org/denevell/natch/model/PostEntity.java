package org.denevell.natch.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.validator.constraints.NotBlank;

public class PostEntity {
	
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
    public List<String> tags;
    public boolean adminEdited;

    public Output() {}

    public Output(PostEntity post) {
      this.username = post.username;
      this.creation = post.created;
      this.modification = post.modified;
      this.subject = StringEscapeUtils.escapeHtml4(post.subject);
      this.content = StringEscapeUtils.escapeHtml4(post.content);
      this.tags = PostEntityUtils.getTagsEscaped(post.tags);
      this.adminEdited = post.adminEdited;
      this.id = post.id;
      this.threadId = post.threadId;
    }
  }

  public static class OutputList {
    public List<Output> posts = new ArrayList<Output>();
    public OutputList() {}

    public OutputList(List<PostEntity> entities) {
      List<Output> outputs = new ArrayList<Output>();
      for (PostEntity p : entities) {
        Output entity = new Output(p);
        outputs.add(entity);
      }
      posts = outputs;
    }
  }


}

package org.denevell.natch.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringEscapeUtils;
import org.denevell.natch.entities.ThreadEntity.AddInput.StringWrapper;
import org.denevell.natch.gen.ServList.OutputWithCount;
import org.denevell.natch.utils.ModelResponse.ModelExternaliser;
import org.denevell.natch.utils.UserGetLoggedInService.Username;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class ThreadEntity implements ModelExternaliser, Username {

	public class OutputList extends OutputWithCount<ThreadEntity>{}

  public String id;
	public long numPosts;
	public PostEntity latestPost;
	public List<PostEntity> posts;
	public PostEntity rootPost;
	
	public ThreadEntity() { }
	
	public ThreadEntity(PostEntity initialPost, List<PostEntity> posts) {
		this.latestPost = initialPost;
		this.rootPost = initialPost;
		this.posts = posts;
	}

  @Override
  public String getUsername() {
    return rootPost.username;
  }

  public static class AddInput {
    @NotBlank(message="Subject cannot be blank")
    @Length(max=PostEntity.MAX_SUBJECT_LENGTH, message="Subject cannot be more than 300 characters")
    public String subject;
    @NotBlank(message="Content cannot be blank") 
    public String content;
    public String threadId;
    @Valid
    public List<StringWrapper> tags;
    public static class StringWrapper {
      @Length(max=PostEntity.MAX_TAG_LENGTH, message="Tag cannot be more than 20 characters")
      public String string;
      public static List<String> toStrings(List<StringWrapper> ws) {
        List<String> ret = new ArrayList<>();
        if(ws!=null) for (StringWrapper w: ws) { 
          if(w!=null && w.string!=null) ret.add(w.string); 
        }
        return ret;
      }
      public static List<StringWrapper> fromStrings(List<String> ws) {
        List<StringWrapper> ret = new ArrayList<>();
        if(ws!=null) for (String w: ws) { 
          StringWrapper sw = new StringWrapper(); 
          sw.string=w; 
          ret.add(sw); 
        }
        return ret;
      }
    }

    public PostEntity adapt(boolean adminEdited, String username) {
      PostEntity entity = new PostEntity();
      long created = new Date().getTime();
      entity.content = content;
      entity.subject = subject;
      entity.threadId = threadId;
      entity.tags = StringWrapper.toStrings(tags);
      entity.username = (username);
      entity.created = (created);
      entity.modified = (created);
      if (adminEdited) {
        entity.adminEdited = true;
      }
      return entity;
    }

    public ThreadEntity adapt(String username) {
      PostEntity entity = adapt(false, username);
      entity.threadId = PostEntity.Utils.createNewThreadId(entity.threadId, entity.subject);
      ThreadEntity threadEntity = new ThreadEntity(entity, Arrays.asList(entity));
      threadEntity.id = (entity.threadId);
      threadEntity.numPosts = (threadEntity.numPosts + 1);
      return threadEntity;
    }
  }

  public static class AddFromPostInput {
    public long postId;
    public String subject;
  }
	
	public static class EditInput {
	  @NotBlank(message="Content cannot be blank")
	  public String content;
	  @NotBlank(message="Subject cannot be blank")
	  @Length(max=PostEntity.MAX_SUBJECT_LENGTH, message="Subject cannot be more than 300 characters") 
	  public String subject;
    @Valid
    public List<StringWrapper> tags;

    public PostEntity adapt() {
      PostEntity entity = new PostEntity();
      entity.subject = subject;
      entity.content = content;
      entity.tags = StringWrapper.toStrings(tags);
      return entity;
    }
	}

	@XmlRootElement
  public static class Output {
    public List<String> tags = new ArrayList<>();
    public String id;
    public String subject;
    public String author;
    public int numPosts;
    public long creation;
    public long modification;
    public long rootPostId;
    public long latestPostId;
    public List<org.denevell.natch.entities.PostEntity.Output> posts = new ArrayList<>();
  }

  @Override
  public Output toOutput() {
    Output output = new Output();
    List<org.denevell.natch.entities.PostEntity.Output> postsResources = new ArrayList<>();
    for (PostEntity p : this.posts) {
        postsResources.add(p.toOutput());
    }
    output.subject = StringEscapeUtils.escapeHtml4(rootPost.subject);
    output.author = rootPost.username;
    output.posts = postsResources;
    output.numPosts = (int) numPosts;
    output.modification = latestPost.modified;
    output.creation = rootPost.created;
    output.id = id;
    output.tags = PostEntity.Utils.getTagsEscaped(rootPost.tags);
    output.rootPostId = rootPost.id;
    output.latestPostId = latestPost.id;
    return output;
  }

        /*if (p.rootPost == null) {
          if (p.id != null) {
            Logger.getLogger(getClass()).info("Found a thread with a null root post. Thread: " + p.id);
          } else {
            Logger.getLogger(getClass()).info("Found a thread with a null root post. Unknown thread id.");
          }
          */

  public static class Utils {
    public static void updateThreadToRemovePost(ThreadEntity te, PostEntity pe) {
      te.posts.remove(pe);
      if (te.rootPost != null && te.rootPost.id == pe.id) {
        te.rootPost = null;
      }
      if (te.latestPost != null && te.latestPost.id == pe.id && te.posts != null && te.posts.size() >= 1) {
        te.latestPost = (te.posts.get(te.posts.size() - 1));
      }
      te.numPosts = (te.numPosts - 1);
    }
  }
	
}

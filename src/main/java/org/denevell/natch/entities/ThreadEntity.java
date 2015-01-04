package org.denevell.natch.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringEscapeUtils;
import org.denevell.natch.entities.ThreadEntity.Utils.StringWrapper;
import org.denevell.natch.gen.ServList.OutputWithCount;
import org.denevell.natch.utils.Adapter.AdapterEdit;
import org.denevell.natch.utils.Adapter.EditableByAll;
import org.denevell.natch.utils.ModelResponse.ModelExternaliser;
import org.denevell.natch.utils.ModelResponse.ModelPushExternaliser;
import org.denevell.natch.utils.UserGetLoggedInService.SystemUser;
import org.denevell.natch.utils.UserGetLoggedInService.Username;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class ThreadEntity implements 
          ModelExternaliser, 
          ModelPushExternaliser<ThreadEntity>,
          Username,
          EditableByAll {

  public String id;
	public long numPosts = 0;
	public PostEntity latestPost;
	public List<PostEntity> posts;
	public PostEntity rootPost;
	
	public ThreadEntity() {}
	
	public ThreadEntity(PostEntity initialPost, List<PostEntity> posts) {
		this.latestPost = initialPost;
		this.rootPost = initialPost;
		this.posts = posts;
	}

  @Override
  public String getUsername() {
    return rootPost.username;
  }

  public static class AddInput implements 
    org.denevell.natch.utils.Adapter.AdapterWithSystemUser<ThreadEntity>
    {
    @NotBlank(message="Subject cannot be blank")
    @Length(max=PostEntity.MAX_SUBJECT_LENGTH, message="Subject cannot be more than 300 characters")
    public String subject;
    @NotBlank(message="Content cannot be blank") 
    public String content;
    public String threadId;
    @Valid
    public List<StringWrapper> tags;

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

    @Override
    public ThreadEntity adaptWithUser(SystemUser user) {
      PostEntity entity = adapt(false, user.getUsername());
      entity.threadId = PostEntity.Utils.createNewThreadId(entity.threadId, entity.subject);
      ThreadEntity threadEntity = new ThreadEntity(entity, Arrays.asList(entity));
      threadEntity.id = (entity.threadId);
      return threadEntity;
    }
  }

  public static class AddFromPostInput {
    public long postId;
    public String subject;
  }
	
	public static class EditInput 
      implements AdapterEdit<PostEntity> {
	  @NotBlank(message="Content cannot be blank")
	  public String content;
	  @NotBlank(message="Subject cannot be blank")
	  @Length(max=PostEntity.MAX_SUBJECT_LENGTH, message="Subject cannot be more than 300 characters") 
	  public String subject;
    @Valid
    public List<StringWrapper> tags;

    @Override
    public PostEntity updateEntity(PostEntity postEntity, SystemUser user) {
      postEntity.content = content;
      if (content!= null) postEntity.content = content;
      if (subject!= null) postEntity.subject = subject;
      if (tags != null) postEntity.tags = StringWrapper.toStrings(tags);
      postEntity.modified = new Date().getTime();
      if (!user.getUsername().equals(postEntity.username) && user.getAdmin()) postEntity.adminEdited = true;
      return postEntity;
    }
	}

	@XmlRootElement
  public static class Output {
    public List<String> tags = new ArrayList<>();
    public String id;
    public String subject;
    public String author;
    public long creation;
    public long modification;
    public PostEntity.Output rootPost;
    public PostEntity.Output latestPost;
    public OutputWithCount<PostEntity.Output> posts;
  }

  @Override
  public Output toOutput() {
    Output output = new Output();

    List<org.denevell.natch.entities.PostEntity.Output> postsResources = new ArrayList<>();
    if(this.posts!=null) {
      for (PostEntity p : this.posts) {
        postsResources.add(p.toOutput());
      }
    }
    OutputWithCount<org.denevell.natch.entities.PostEntity.Output> outputWithCount = 
        new OutputWithCount<>(postsResources, 0);

    output.posts = outputWithCount;

    output.subject = StringEscapeUtils.escapeHtml4(rootPost.subject);
    output.author = rootPost.username;
    output.modification = latestPost.modified;
    output.creation = rootPost.created;
    output.id = id;
    output.tags = PostEntity.Utils.getTagsEscaped(rootPost.tags);
    if(this.rootPost!=null) {
      output.rootPost = this.rootPost.toOutput();
    }
    if(this.latestPost!=null) {
      output.latestPost = this.latestPost.toOutput();
    }
    return output;
  }

        /*if (p.rootPost == null) {
          if (p.id != null) {
            Logger.getLogger(getClass()).info("Found a thread with a null root post. Thread: " + p.id);
          } else {
            Logger.getLogger(getClass()).info("Found a thread with a null root post. Unknown thread id.");
          }
          */

  @Override
  public Object toPushResource(ThreadEntity t) {
    return new ThreadResourceForPush(t);
  }

  public static class ThreadResourceForPush {

    public List<String> tags;
    public String id;
    public String subject;
    public String author;
    public long numPosts;
    public long creation;
    public long modification;
    public long rootPostId;

    public ThreadResourceForPush() {}
    
    public ThreadResourceForPush(ThreadEntity tr) {
      subject = StringEscapeUtils.escapeHtml4(tr.rootPost.subject);
      author = tr.rootPost.username;
      //TODO: postsNum?
      tags = PostEntity.Utils.getTagsEscaped(tr.rootPost.tags);
      rootPostId = tr.rootPost.id;
      modification = tr.rootPost.modified;
      creation = tr.rootPost.created;
      id = tr.id;
    }

  }

	public class OutputList extends OutputWithCount<ThreadEntity>{}

  public static class Utils {
    public static void updateThreadToRemovePost(ThreadEntity te, PostEntity pe) {
      te.posts.remove(pe);
      if (te.rootPost != null && te.rootPost.id == pe.id) {
        te.rootPost = null;
      }
      if (te.latestPost != null && te.latestPost.id == pe.id && te.posts != null && te.posts.size() >= 1) {
        te.latestPost = (te.posts.get(te.posts.size() - 1));
      }
    }
    public static List<StringWrapper> stringsToStringWrapper(List<String> ws) {
        List<StringWrapper> ret = new ArrayList<>();
        if(ws!=null) for (String w: ws) { 
          StringWrapper sw = new StringWrapper(); 
          sw.string=w; 
          ret.add(sw); 
        }
        return ret;
    }
    public static List<String> stringWrappersToStrings(List<StringWrapper> ws) {
        List<String> ret = new ArrayList<>();
        if(ws!=null) for (StringWrapper w: ws) { 
          if(w!=null && w.string!=null) ret.add(w.string); 
        }
        return ret;
    }
    public static class StringWrapper {
      @Length(max=PostEntity.MAX_TAG_LENGTH, message="Tag cannot be more than 20 characters")
      public String string;
      public static List<String> toStrings(List<StringWrapper> ws) {
        return Utils.stringWrappersToStrings(ws);
      }
      public static List<StringWrapper> fromStrings(List<String> ws) {
        return Utils.stringsToStringWrapper(ws);
      }
    }
  }
	
}

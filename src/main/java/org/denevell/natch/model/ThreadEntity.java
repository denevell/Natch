package org.denevell.natch.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringEscapeUtils;
import org.denevell.natch.model.ThreadEntity.AddInput.StringWrapper;
import org.denevell.natch.model.ThreadsListModel.ThreadsAndNumTotalThreads;
import org.denevell.natch.utils.Log;
import org.denevell.natch.utils.ModelResponse.ModelExternaliser;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

public class ThreadEntity implements ModelExternaliser {
	public static final String NAMED_QUERY_LIST_THREADS = "findThreads";
	public static final String NAMED_QUERY_LIST_THREADS_BY_TAG = "findThreadByTag";
	public static final String NAMED_QUERY_FIND_THREAD_BY_ID = "findThreadById";
	public static final String NAMED_QUERY_FIND_AUTHOR = "findAuthorById";
	public static final String NAMED_QUERY_COUNT_THREADS= "countThreads";
	public static final String NAMED_QUERY_COUNT_THREAD_BY_TAG = "countThreadsWithTag";
	public static final String NAMED_QUERY_PARAM_ID = "id";
	public static final String NAMED_QUERY_PARAM_TAG = "tag";
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
    public List<org.denevell.natch.model.PostEntity.Output> posts = new ArrayList<>();
  }

  @Override
  public Output toOutput() {
    Output output = new Output();
    List<org.denevell.natch.model.PostEntity.Output> postsResources = new ArrayList<>();
    for (PostEntity p : this.posts) {
        postsResources.add(p.toOutput());
    }
    output.subject = StringEscapeUtils.escapeHtml4(rootPost.subject);
    output.author = rootPost.username;
    output.posts = postsResources;
    output.numPosts = (int) numPosts;
    output.id = id;
    output.tags = PostEntity.Utils.getTagsEscaped(rootPost.tags);
    return output;
  }

  @XmlRootElement
  public static class OutputList {
    public long numOfThreads;
    public List<Output> threads = new ArrayList<Output>();

    public OutputList() {}

		public OutputList(ThreadsAndNumTotalThreads threadsAndNumTotalThreads) {
		  this.numOfThreads = threadsAndNumTotalThreads.getNumOfThreads();
			ArrayList<Output> postsResources = new ArrayList<Output>();
			for (ThreadEntity p: threadsAndNumTotalThreads.getThreads()) {
        if (p.rootPost == null) {
          if (p.id != null) {
            Log.info(getClass(), "Found a thread with a null root post. Thread: " + p.id);
          } else {
            Log.info(getClass(), "Found a thread with a null root post. Unknown thread id.");
          }
          continue;
				} else {
					Output postResource = new Output();
					postResource.author = (p.rootPost.username);
					postResource.numPosts = ((int) p.numPosts);
					postResource.subject = StringEscapeUtils.escapeHtml4(p.rootPost.subject);
					postResource.rootPostId = (p.rootPost.id);
					postResource.tags = PostEntity.Utils.getTagsEscaped(p.rootPost.tags);
					postResource.modification = (p.latestPost.modified);
					postResource.creation = (p.rootPost.created);
					postResource.id = (p.id);
					postResource.latestPostId = (p.latestPost.id);
					postsResources.add(postResource);
				}
			}
			this.threads = (postsResources);
		}
  }

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

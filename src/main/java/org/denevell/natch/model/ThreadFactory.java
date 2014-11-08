package org.denevell.natch.model;

import java.util.Arrays;
import java.util.List;

  public class ThreadFactory {

    public ThreadEntity makeThread() {
      return null;
    }

    public ThreadEntity makeThread(PostEntity p) {
      p.threadId = PostEntityUtils.createNewThreadId(p.threadId, p.subject);
      ThreadEntity threadEntity = new ThreadEntity(p, Arrays.asList(p));
      threadEntity.id = (p.threadId);
      threadEntity.numPosts = (threadEntity.numPosts + 1);
      return threadEntity;
    }

    /**
     * Making a thread based on an existing thread
     */
    public ThreadEntity makeThread(ThreadEntity thread, PostEntity p) {
      p.subject = (thread.rootPost.subject);
      thread.latestPost = p;
      List<PostEntity> posts = thread.posts;
      if (posts != null) {
        posts.add(p);
      } else {
        posts = Arrays.asList(p);
      }
      thread.posts = (posts);
      thread.numPosts = (thread.numPosts + 1);
      return thread;
    }

  }

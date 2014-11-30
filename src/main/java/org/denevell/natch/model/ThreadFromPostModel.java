package org.denevell.natch.model;

import java.util.Arrays;
import java.util.HashMap;

import org.denevell.jrappy.Jrappy;
import org.denevell.jrappy.Jrappy.DeleteOrMerge;
import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.ModelResponse;
import org.denevell.natch.utils.Responses;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface ThreadFromPostModel {
  // TODO: Return a proper object
  public ModelResponse<HashMap<String, String>> makeNewThread(long postId, String subject, boolean isAdmin);

  @Service
  public static class ThreadFromPostModelImpl implements ThreadFromPostModel {

    private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);
    private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);

    @Override
    public ModelResponse<HashMap<String, String>> makeNewThread(long postId, String subject, boolean isAdmin) {
      if(!isAdmin) {
        return new ModelResponse<>(403, null);
      }
      try {
        PostEntity post = mPostModel.startTransaction().find(postId, true, PostEntity.class);
        if (post == null) {
          return new ModelResponse<>(404, null);
        }
        PostEntity newPost = new PostEntity(post);
        newPost.id = (0);
        newPost.adminEdited = true;
        newPost.threadId = PostEntity.Utils.createNewThreadId(null, subject);
        newPost.subject = (subject);
        removePostFromOldThread(post);
        mPostModel.getEntityManager().remove(post);
        ThreadEntity thread = createNewThreadFromPost(newPost);
        if(thread==null) {
          return new ModelResponse<>(500, null);
        }
        return new ModelResponse<>(200, Responses.hM("threadId", thread.id));
      } finally {
        mPostModel.commitAndCloseEntityManager();
      }
    }

    private void removePostFromOldThread(final PostEntity post) {
      mThreadModel.useTransaction(mPostModel.getEntityManager()).findAndUpdateOrDelete(post.threadId, new DeleteOrMerge<ThreadEntity>() {
        @Override
        public boolean shouldDelete(ThreadEntity item) {
          ThreadEntity.Utils.updateThreadToRemovePost(item, post);
          return item.posts == null || item.posts.size() == 0;
        }
      }, ThreadEntity.class);
    }

    private ThreadEntity createNewThreadFromPost(final PostEntity post) {
      ThreadEntity thread = mThreadModel.useTransaction(mPostModel.getEntityManager()).createOrUpdate(null, new Jrappy.UpdateItem<ThreadEntity>() {
        @Override
        public ThreadEntity update(ThreadEntity item) {
          return null;
        }
      }, new Jrappy.NewItem<ThreadEntity>() {
        @Override
        public ThreadEntity newItem() {
          post.threadId = PostEntity.Utils.createNewThreadId(post.threadId, post.subject);
          ThreadEntity threadEntity = new ThreadEntity(post, Arrays.asList(post));
          threadEntity.id = (post.threadId);
          threadEntity.numPosts = (threadEntity.numPosts + 1);
          return threadEntity;
        }
      }, ThreadEntity.class);
      return thread;
    }

  }

}

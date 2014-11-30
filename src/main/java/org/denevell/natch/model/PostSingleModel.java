package org.denevell.natch.model;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.ModelResponse;
import org.jvnet.hk2.annotations.Service;

public interface PostSingleModel {
  /**
   * @param id
   * @return PostEntity with the subject of the thread
   */
  ModelResponse<PostEntity> find(long id);

  @Service
  public static class PostSingleModelImpl implements PostSingleModel {

    private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);
    private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);

    public ModelResponse<PostEntity> find(long id) {
      try {
        PostEntity post = mPostModel.startTransaction().namedQuery(PostEntity.NAMED_QUERY_FIND_BY_ID).queryParam("id", id).single(PostEntity.class);
        if (post == null) {
          return new ModelResponse<PostEntity>(404, null);
        } else {
          ThreadEntity thread = mThreadModel.useTransaction(mPostModel.getEntityManager()).namedQuery(ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID)
              .queryParam("id", post.threadId).single(ThreadEntity.class);
          post.subject = (thread.rootPost.subject);
          return new ModelResponse<PostEntity>(200, post);
        }

      } finally {
        mThreadModel.commitAndCloseEntityManager();
      }
    }

  }
}

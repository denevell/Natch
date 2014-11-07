package org.denevell.natch.model;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

public interface PostSingleModel {
  /**
   * @param id
   * @return PostEntity with the subject of the thread
   */
  PostEntity find(long id);

  @Service
  public static class PostSingleModelImpl implements PostSingleModel {

    private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);
    private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);

    public PostEntity find(long id) {
      try {
        PostEntity post = mPostModel.startTransaction().namedQuery(PostEntity.NAMED_QUERY_FIND_BY_ID).queryParam("id", id).single(PostEntity.class);
        if (post == null) {
          return null;
        } else {
          ThreadEntity thread = mThreadModel.useTransaction(mPostModel.getEntityManager()).namedQuery(ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID)
              .queryParam("id", post.getThreadId()).single(ThreadEntity.class);
          post.setSubject(thread.getRootPost().getSubject());
          return post;
        }

      } finally {
        mThreadModel.commitAndCloseEntityManager();
      }
    }

  }
}

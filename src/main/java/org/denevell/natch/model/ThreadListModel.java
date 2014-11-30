package org.denevell.natch.model;

import java.util.List;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.entities.PostEntity;
import org.denevell.natch.entities.ThreadEntity;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.ModelResponse;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface ThreadListModel {
	ModelResponse<ThreadEntity> find(String threadId, int start, int maxNumPosts);

@Service
public static class ThreadListModelImpl implements ThreadListModel {

  private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(
      JPAFactoryContextListener.sFactory);
  private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(
      JPAFactoryContextListener.sFactory);

  @Override
  public ModelResponse<ThreadEntity> find(String id, int start, int maxNumPosts) {
    try {
      List<PostEntity> posts = null;
      ThreadEntity thread = null;
      posts = mPostModel.startTransaction().start(start).max(maxNumPosts)
          .namedQuery(PostEntity.NAMED_QUERY_FIND_BY_THREADID)
          .queryParam("threadId", id).list(PostEntity.class);
      if (posts != null) {
        thread = mThreadModel.useTransaction(mPostModel.getEntityManager())
            .namedQuery(ThreadEntity.NAMED_QUERY_FIND_THREAD_BY_ID)
            .queryParam("id", id).single(ThreadEntity.class);
        mThreadModel.closeEntityManager();
      }
      if (posts == null || thread == null || posts.size() == 0) {
        return new ModelResponse<ThreadEntity>(404, null);
      } else {
        thread.posts = posts;
        return new ModelResponse<ThreadEntity>(200, thread);
      }
    } finally {
      mPostModel.commitAndCloseEntityManager();

    }
  }

}
}

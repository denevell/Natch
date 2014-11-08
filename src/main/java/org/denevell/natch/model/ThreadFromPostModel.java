package org.denevell.natch.model;

import org.denevell.jrappy.Jrappy;
import org.denevell.jrappy.Jrappy.DeleteOrMerge;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface ThreadFromPostModel {
  public ThreadEntity makeNewThread(long postId, String subject);

  @Service
  public static class ThreadFromPostModelImpl implements ThreadFromPostModel {

    private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);
    private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);

    @Override
    public ThreadEntity makeNewThread(long postId, String subject) {
      try {
        PostEntity post = mPostModel.startTransaction().find(postId, true, PostEntity.class);
        if (post == null)
          return null;
        PostEntity newPost = new PostEntity(post);
        newPost.id = (0);
        newPost.adminEdited = true;
        newPost.threadId = PostEntityUtils.createNewThreadId(null, subject);
        newPost.subject = (subject);
        removePostFromOldThread(post);
        mPostModel.getEntityManager().remove(post);
        ThreadEntity thread = createNewThreadFromPost(newPost);
        return thread;
      } finally {
        mPostModel.commitAndCloseEntityManager();
      }
    }

    private void removePostFromOldThread(final PostEntity post) {
      mThreadModel.useTransaction(mPostModel.getEntityManager()).findAndUpdateOrDelete(post.threadId, new DeleteOrMerge<ThreadEntity>() {
        @Override
        public boolean shouldDelete(ThreadEntity item) {
          ThreadEntityUtils.updateThreadToRemovePost(item, post);
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
          return new ThreadFactory().makeThread(post);
        }
      }, ThreadEntity.class);
      return thread;
    }

  }

}

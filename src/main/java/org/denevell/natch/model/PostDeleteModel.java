package org.denevell.natch.model;

import javax.persistence.EntityManager;

import org.denevell.jrappy.Jrappy;
import org.denevell.jrappy.Jrappy.DeleteOrMerge;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface PostDeleteModel {
  public static int DELETED = 0;
  public static int NOT_YOURS = 1;
  public static int DOESNT_EXIST = 2;

  int delete(long id, String user, boolean adminEditing);

  @Service
  public static class PostDeleteModelImpl implements PostDeleteModel {

    private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);
    private Jrappy<ThreadEntity> mThreadModel = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);

    /**
     * Doesn't close the entity manager
     */
    public int delete(long id, String username, boolean adminEditing) {
      try {
        final PostEntity pe = mPostModel.startTransaction().find(id, false, PostEntity.class);
        EntityManager postEntityManager = mPostModel.getEntityManager();
        if (pe == null) {
          mPostModel.commitAndCloseEntityManager();
          return PostDeleteModel.DOESNT_EXIST;
        } else if (!adminEditing && !pe.getUsername().equals(username)) {
          mPostModel.commitAndCloseEntityManager();
          return PostDeleteModel.NOT_YOURS;
        }
        mThreadModel.useTransaction(postEntityManager).findAndUpdateOrDelete(pe.getThreadId(), new DeleteOrMerge<ThreadEntity>() {
          @Override
          public boolean shouldDelete(ThreadEntity item) {
            item.updateThreadToRemovePost(pe);
            return item.getPosts() == null || item.getPosts().size() == 0;
          }
        }, ThreadEntity.class);
        postEntityManager.remove(pe);
        return PostDeleteModel.DELETED;

      } finally {
        mThreadModel.commitAndCloseEntityManager();
      }
    }

  }
}

package org.denevell.natch.model;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface PostAddModel {
  ThreadEntity add(PostEntity postEntity);

  @Service
  public static class PostAddModelImpl implements PostAddModel {

    final ThreadFactory mThreadFactory = new ThreadFactory();

    @Override
    public ThreadEntity add(final PostEntity postEntity) {
      Jrappy<ThreadEntity> model = new Jrappy<ThreadEntity>(JPAFactoryContextListener.sFactory);
      try {
        model.startTransaction();
        ThreadEntity thread = model.createOrUpdate(postEntity.getThreadId(), new Jrappy.UpdateItem<ThreadEntity>() {
          @Override
          public ThreadEntity update(ThreadEntity item) {
            return mThreadFactory.makeThread(item, postEntity);
          }
        }, new Jrappy.NewItem<ThreadEntity>() {
          @Override
          public ThreadEntity newItem() {
            return mThreadFactory.makeThread(postEntity);
          }
        }, ThreadEntity.class);
        return thread;

      } finally {
        model.commitAndCloseEntityManager();
      }
    }

  }
}

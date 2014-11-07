package org.denevell.natch.model;

import javax.inject.Singleton;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface PostFindModel {

  PostEntity find(long id, boolean pessemesticRead);

  @Service
  @Singleton
  public static class PostFindModelImpl implements PostFindModel {

    private Jrappy<PostEntity> mPostModel = new Jrappy<PostEntity>(JPAFactoryContextListener.sFactory);

    public PostEntity find(long id, boolean pessmesticRead) {
      PostEntity f = mPostModel.find(id, pessmesticRead, PostEntity.class);
      return f;
    }

  }

}

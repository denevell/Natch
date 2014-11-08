package org.denevell.natch.model;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface PushAddModel {
  void add(PushEntity entity);

  @Service
  public static class PushAddModelImpl implements PushAddModel {
    private Jrappy<PushEntity> model = new Jrappy<PushEntity>(JPAFactoryContextListener.sFactory);

    public void add(final PushEntity entity) {
      try {
        model.startTransaction().queryParam("id", entity.clientId).addIfDoesntExist(PushEntity.NAMED_QUERY_FIND_ID, entity);
      } finally {
        model.commitAndCloseEntityManager();
      }
    }

  }
}

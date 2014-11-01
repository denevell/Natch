package org.denevell.natch.model.impl;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.PushEntity;
import org.denevell.natch.model.interfaces.PushAddModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

@Service
public class PushAddModelImpl implements PushAddModel {
  private Jrappy<PushEntity> model = new Jrappy<PushEntity>(
      JPAFactoryContextListener.sFactory);

  public void add(final PushEntity entity) {
    try {
      model.startTransaction().queryParam("id", entity.getClientId())
          .addIfDoesntExist(PushEntity.NAMED_QUERY_FIND_ID, entity);
    } finally {
      model.commitAndCloseEntityManager();
    }
  }

}
package org.denevell.natch.model.impl;

import java.util.List;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.model.entities.PushEntity;
import org.denevell.natch.model.interfaces.PushListModel;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.jvnet.hk2.annotations.Service;

@Service
public class PushListModelImpl implements PushListModel {
  private Jrappy<PushEntity> model = new Jrappy<PushEntity>(
      JPAFactoryContextListener.sFactory);

  public List<PushEntity> list() {
    try {
      List<PushEntity> list = model.startTransaction()
          .namedQuery(PushEntity.NAMED_QUERY_LIST_IDS).list(PushEntity.class);
      return list;
    } finally {
      model.commitAndCloseEntityManager();
    }
  }

}
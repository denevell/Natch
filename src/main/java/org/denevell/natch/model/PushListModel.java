package org.denevell.natch.model;

import java.util.List;

import org.denevell.jrappy.Jrappy;
import org.denevell.natch.entities.PushEntity;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.glassfish.jersey.spi.Contract;
import org.jvnet.hk2.annotations.Service;

@Contract
public interface PushListModel {
  List<PushEntity> list();

  @Service
  public static class PushListModelImpl implements PushListModel {
    private Jrappy<PushEntity> model = new Jrappy<PushEntity>(JPAFactoryContextListener.sFactory);

    public List<PushEntity> list() {
      try {
        List<PushEntity> list = model.startTransaction().namedQuery(PushEntity.NAMED_QUERY_LIST_IDS).list(PushEntity.class);
        return list;
      } finally {
        model.commitAndCloseEntityManager();
      }
    }

  }
}
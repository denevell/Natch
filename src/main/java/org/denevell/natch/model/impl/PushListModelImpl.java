package org.denevell.natch.model.impl;

import java.util.List;

import javax.inject.Singleton;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.model.entities.PushEntity;
import org.denevell.natch.model.interfaces.PushListModel;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PushListModelImpl implements PushListModel {
	private CallDbBuilder<PushEntity> model = new CallDbBuilder<PushEntity>();

	public List<PushEntity> list() {
		List<PushEntity> list = model
			.startTransaction()
			.namedQuery(PushEntity.NAMED_QUERY_LIST_IDS)
			.list(PushEntity.class);
		model.commitAndCloseEntityManager();
		return list;
	}

}
package org.denevell.natch.model.impl;

import javax.inject.Singleton;

import org.denevell.natch.db.CallDbBuilder;
import org.denevell.natch.model.entities.PushEntity;
import org.denevell.natch.model.interfaces.PushAddModel;
import org.jvnet.hk2.annotations.Service;

@Service @Singleton
public class PushAddModelImpl implements PushAddModel {
	private CallDbBuilder<PushEntity> model = new CallDbBuilder<PushEntity>();

	public void add(final PushEntity entity) {
		model
			.startTransaction()
			.queryParam("id", entity.getClientId())
			.addIfDoesntExist(PushEntity.NAMED_QUERY_FIND_ID, entity);
		model.commitAndCloseEntityManager();
	}

}
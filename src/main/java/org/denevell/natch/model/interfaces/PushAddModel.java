package org.denevell.natch.model.interfaces;

import org.denevell.natch.model.entities.PushEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface PushAddModel {
	void add(PushEntity entity);
}

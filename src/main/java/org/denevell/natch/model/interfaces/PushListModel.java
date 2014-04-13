package org.denevell.natch.model.interfaces;

import java.util.List;

import org.denevell.natch.model.entities.PushEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface PushListModel {
	List<PushEntity> list();
}

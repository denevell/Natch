package org.denevell.natch.model.interfaces;

import org.denevell.natch.db.entities.PostEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface PostFindModel {
	PostEntity find(long id, boolean pessemesticRead);
}

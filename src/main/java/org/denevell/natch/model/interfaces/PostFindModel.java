package org.denevell.natch.model.interfaces;

import org.denevell.natch.model.entities.PostEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface PostFindModel {
	PostEntity find(long id, boolean pessemesticRead);
}

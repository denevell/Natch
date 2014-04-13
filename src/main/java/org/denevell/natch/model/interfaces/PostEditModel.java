package org.denevell.natch.model.interfaces;

import org.denevell.natch.db.entities.PostEntity;
import org.denevell.natch.db.entities.UserEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface PostEditModel {
	public static int EDITED = 0;
	public static int NOT_YOURS = 1;
	public static int DOESNT_EXIST = 2;
	int edit(long id, 
			UserEntity user, 
			PostEntity postWithEditedData);
}

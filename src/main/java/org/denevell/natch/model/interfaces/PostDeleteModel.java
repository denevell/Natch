package org.denevell.natch.model.interfaces;

import org.denevell.natch.db.entities.UserEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface PostDeleteModel {
	public static int DELETED = 0;
	public static int NOT_YOURS = 1;
	public static int DOESNT_EXIST = 2;
	int delete(long id, UserEntity user);
}

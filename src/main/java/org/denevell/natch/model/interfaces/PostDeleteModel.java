package org.denevell.natch.model.interfaces;

import org.glassfish.jersey.spi.Contract;

@Contract
public interface PostDeleteModel {
	public static int DELETED = 0;
	public static int NOT_YOURS = 1;
	public static int DOESNT_EXIST = 2;
	int delete(long id, String user, boolean adminEditing);
}

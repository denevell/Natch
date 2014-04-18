package org.denevell.natch.model.interfaces;

import org.denevell.natch.model.entities.PostEntity;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface PostEditModel {
	public static int EDITED = 0;
	public static int NOT_YOURS = 1;
	public static int DOESNT_EXIST = 2;
	/**
	 * @return -1 on error on a static constant in this class
	 */
	int edit(long id, 
			String user, 
			PostEntity postWithEditedData,
			boolean adminEditing);
}

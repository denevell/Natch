package org.denevell.natch.model.interfaces;

import org.denevell.natch.model.entities.UserEntity;

public interface UserAddModel {
	
	public static int ADDED = 0;
	public static int EMAIL_ALREADY_EXISTS = 1;
	public static int USER_ALREADY_EXISTS= 2;

	public int add(final UserEntity user);

}

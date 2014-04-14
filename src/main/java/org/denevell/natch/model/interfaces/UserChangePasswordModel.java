package org.denevell.natch.model.interfaces;

public interface UserChangePasswordModel {

	public final static int NOT_FOUND = 1;
	public final static int CHANGED = 0;
	int changePassword(String username, String password);

}

package org.denevell.natch.model.interfaces;


public interface UserPasswordResetDeleteModel {

	public final static int CANT_FIND = 1;
	public final static int UPDATED = 0;
	int deleteRequest(String username);

}

package org.denevell.natch.model.interfaces;

public interface UserAdminToggleModel {

	public static int CANT_FIND = 1;
	public static int TOGGLED = 0;

	public int toggleAdmin(final String userId);
}

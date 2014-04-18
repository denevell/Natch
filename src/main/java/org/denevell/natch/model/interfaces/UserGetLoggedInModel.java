package org.denevell.natch.model.interfaces;

import org.denevell.natch.io.users.User;

public interface UserGetLoggedInModel {

	User get(Object authObject);

}

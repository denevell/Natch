package org.denevell.natch.model.interfaces;

import org.denevell.natch.model.entities.UserEntity;

public interface UserGetLoggedInModel {

	UserEntity get(Object authObject);

}

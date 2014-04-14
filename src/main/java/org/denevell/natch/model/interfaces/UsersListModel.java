package org.denevell.natch.model.interfaces;

import java.util.List;

import org.denevell.natch.model.entities.UserEntity;

public interface UsersListModel {

	List<UserEntity> list(int start, int limit);

}

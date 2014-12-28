package org.denevell.natch.utils;

import org.denevell.natch.utils.UserGetLoggedInService.SystemUser;


public interface Adapter<T> {
  
  T adapt();

  public static interface AdapterWithSystemUser<T> {
    T adaptWithUser(SystemUser user);
  }

  public static interface AdapterEdit<T> {
    T updateEntity(T object, SystemUser user);
  }

  public static interface EditableByAll {}

}

package org.denevell.natch.utils;

import org.denevell.natch.utils.UserGetLoggedInService.SystemUser;


public interface Adapter<T> {
  
  T adapt();

  public static interface AdapterWithSystemUser<T> {
    T adapt(SystemUser user);
  }

}

package org.denevell.natch.utils;

import java.util.HashMap;

public class Responses {

	@SuppressWarnings("serial")
  public static HashMap<String, String> hM(final String key, final String val) {
    return new HashMap<String, String>() {{ put(key, val); }};
  }

}

package org.denevell.natch.auth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginAuthKeysSingleton {
	private static LoginAuthKeysSingleton sInstance;
	Map<String, String> mLoginKeys = Collections.synchronizedMap(new HashMap<String, String>());
	
	private LoginAuthKeysSingleton() {
	}
	
	public static LoginAuthKeysSingleton getInstance() {
		if(sInstance==null) {
			sInstance = new LoginAuthKeysSingleton();
		} 
		return sInstance;
	}

	public String generate(String username) {
		if(username==null || username.trim().length()==0) {
			return null;
		}
		String key = UUID.randomUUID().toString();
		mLoginKeys.put(key, username);
		return key;
	}

	public String retrieveUsername(String key) {
		String username = mLoginKeys.get(key);
		return username;
	}

	public void clearAllKeys() {
		mLoginKeys.clear();
	}

}

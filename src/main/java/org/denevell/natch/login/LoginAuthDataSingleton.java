package org.denevell.natch.login;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginAuthDataSingleton {
	private static LoginAuthDataSingleton sInstance;
	Map<String, String> mLoginKeys = Collections.synchronizedMap(new HashMap<String, String>());
	
	private LoginAuthDataSingleton() {
	}
	
	public static LoginAuthDataSingleton getInstance() {
		if(sInstance==null) {
			sInstance = new LoginAuthDataSingleton();
		} 
		return sInstance;
	}

	public String generate(String username) {
		if(username==null || username.trim().length()==0) {
			return null;
		}
		String key = UUID.randomUUID().toString();
		mLoginKeys.put(username, key);
		return key;
	}

	public String retrieve(String username) {
		String key = mLoginKeys.get(username);
		return key;
	}

	public void clearAllKeys() {
		mLoginKeys.clear();
	}

}

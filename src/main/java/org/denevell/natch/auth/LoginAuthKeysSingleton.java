package org.denevell.natch.auth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
		// Remove entry for this value if exists already
		Set<Entry<String, String>> entries = mLoginKeys.entrySet();
		for (Entry<String, String> entry : entries) {
			if(entry.getValue().equals(username)) {
				mLoginKeys.remove(entry.getKey());
				break;
			}
		}
		String key = UUID.randomUUID().toString();
		mLoginKeys.put(key, username);
		return key;
	}

	public String retrieveUsername(String key) {
		String username = mLoginKeys.get(key);
		return username;
	}
	
	public void remove(String authKey) {
		mLoginKeys.remove(authKey);
	}

	public void clearAllKeys() {
		mLoginKeys.clear();
	}

}

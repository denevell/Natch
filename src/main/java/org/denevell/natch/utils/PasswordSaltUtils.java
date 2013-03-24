package org.denevell.natch.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordSaltUtils {
	
	public String generatedSaltedPassword(String password) {
		password = BCrypt.hashpw(password, BCrypt.gensalt(12));
		return password;
	}

	public boolean checkSaltedPassword(String attempt, String saltedPassword) {
		boolean res = BCrypt.checkpw(attempt, saltedPassword);
		return res;
	}

}

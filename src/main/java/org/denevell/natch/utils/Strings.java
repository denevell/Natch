package org.denevell.natch.utils;

import java.util.ResourceBundle;

public class Strings {
	
	public static ResourceBundle getMainResourceBundle() {
		return ResourceBundle.getBundle("Strings");
	}
	
	public static String unknown_error = "unknown_error";
	public static String incorrect_username_or_password = "incorrect_username";
	public static String username_already_exists = "username_exists";
	public static String user_pass_cannot_be_blank= "user_pass_cant_be_blank";
	public static String post_fields_cannot_be_blank= "post_fields_cannot_be_blank";

}

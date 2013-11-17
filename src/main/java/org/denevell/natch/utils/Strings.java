package org.denevell.natch.utils;

import java.util.ResourceBundle;

public class Strings {
	
	public static ResourceBundle getMainResourceBundle() {
		return ResourceBundle.getBundle("Strings");
	}
	
	public static final String post_doesnt_exist = "post_doesnt_exist";
	public static final String post_not_yours = "post_not_yours";
	public static final String unknown_error = "unknown_error";
	public static final String incorrect_username_or_password = "incorrect_username";
	public static final String username_already_exists = "username_exists";
	public static final String user_pass_cannot_be_blank= "user_pass_cant_be_blank";
	public static final String post_fields_cannot_be_blank= "post_fields_cannot_be_blank";
    public static final String tag_too_large = "tag_too_large";
    public static final String subject_too_large = "subject_too_large";

}

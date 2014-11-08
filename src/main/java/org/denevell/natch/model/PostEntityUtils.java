package org.denevell.natch.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.denevell.natch.utils.Log;

public class PostEntityUtils {
	
	public static String createNewThreadId(String threadId, String subject) {
    if ((threadId == null || threadId.trim().length() == 0)) {
      long created = new Date().getTime();
      try {
        MessageDigest md5Algor = MessageDigest.getInstance("MD5");
        StringBuffer sb = new StringBuffer();
        if(subject==null || subject.isEmpty()) {
          subject = "-";
        }
        byte[] digest = md5Algor.digest(subject.getBytes());
        for (byte b : digest) {
          sb.append(Integer.toHexString((int) (b & 0xff)));
        }
        threadId = sb.toString();
      } catch (NoSuchAlgorithmException e) {
        Log.info(PostEntityUtils.class, "Couldn't get an MD5 hash. I guess we'll just use hashCode() then.");
        e.printStackTrace();
        threadId = String.valueOf(subject.hashCode());
      }
      threadId = threadId + String.valueOf(created);
    }
		return threadId;
	}

	public static List<String> getTagsEscaped(List<String> tags) {
		if(tags==null) return null;
		for (int i = 0; i < tags.size(); i++) {
			String string = StringEscapeUtils.escapeHtml4(tags.get(i));
			tags.set(i, string);
		}
		return tags;
	}

}

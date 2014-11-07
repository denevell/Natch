package org.denevell.natch.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.denevell.natch.utils.Log;

public class PostEntity {
	
	public static final String NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE = "findByModData";
	public static final String NAMED_QUERY_FIND_BY_THREADID = "findByThreadId";
	public static final String NAMED_QUERY_PARAM_ID= "id";
	public static final String NAMED_QUERY_PARAM_THREADID = "threadId";
	public static final String NAMED_QUERY_FIND_BY_ID = "findById";
  public static final int MAX_TAG_LENGTH = 20;
  public static final int MAX_SUBJECT_LENGTH = 300;
	
	public long id;
	public long created;
	public long modified;
	public String subject;
	public String content;
	public String threadId;
	public List<String> tags;
	public String username;
  public boolean adminEdited = false;
	
	public PostEntity() {
	}
	
	public PostEntity(String user, long created, long modified, String subject, String content, String threadId) {
		this.username = user;
		this.created = created;
		this.modified = modified;
		this.subject = subject;
		this.content = content;
		this.threadId = threadId;
	}

	public PostEntity(PostEntity post) {
		this.id = post.id;
		this.created = post.created;
		this.modified = post.modified;
		this.subject = post.subject;
		this.content = post.content;
		this.threadId = post.threadId;
		this.tags = post.tags;
		this.username = post.username;
		this.adminEdited = post.adminEdited;
	}

  public static boolean isSubjectTooLarge(String subject) {
    return subject != null && subject.length() > PostEntity.MAX_SUBJECT_LENGTH;
  }

  public static boolean isTagLengthOkay(List<String> tags) {
    if (tags != null && tags.size() > 0) {
      for (String tag : tags) {
        if (tag != null & tag.length() > PostEntity.MAX_TAG_LENGTH) {
          return false;
        }
      }
    }
    return true;
  }

	public String getSubject() {
		String escaped = StringEscapeUtils.escapeHtml4(subject);
		return escaped;
	}

	public String getContent() {
		String escaped = StringEscapeUtils.escapeHtml4(content);
		return escaped;
	}

	public void setThreadId(String threadId) {
		if(threadId==null) {
			long created = new Date().getTime();
			threadId = getThreadId(subject, threadId, created);
		}
		this.threadId = threadId;
	}

	public List<String> getTags() {
		if(tags==null) return null;
		for (int i = 0; i < tags.size(); i++) {
			String string = StringEscapeUtils.escapeHtml4(tags.get(i));
			tags.set(i, string);
		}
		return tags;
	}

  private String getThreadId(String subject, String threadId, long time) {
    if (threadId == null || threadId.trim().length() == 0) {
      try {
        MessageDigest md5Algor = MessageDigest.getInstance("MD5");
        StringBuffer sb = new StringBuffer();
        byte[] digest = md5Algor.digest(subject.getBytes());
        for (byte b : digest) {
          sb.append(Integer.toHexString((int) (b & 0xff)));
        }
        threadId = sb.toString();
      } catch (NoSuchAlgorithmException e) {
        Log.info(getClass(), "Couldn't get an MD5 hash. I guess we'll just use hashCode() then.");
        e.printStackTrace();
        threadId = String.valueOf(subject.hashCode());
      }
      threadId = threadId + String.valueOf(time);
    }
    return threadId;
  }

}

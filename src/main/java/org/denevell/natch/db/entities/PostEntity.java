package org.denevell.natch.db.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@NamedQueries({
	@NamedQuery(name=PostEntity.NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE,query=
		"select p from PostEntity p order by p.modified"),
	@NamedQuery(name=PostEntity.NAMED_QUERY_FIND_BY_THREADID,query=
		"select p from PostEntity p where p.threadId = :"+PostEntity.NAMED_QUERY_PARAM_THREADID
	    + " order by p.created"),
	@NamedQuery(name=PostEntity.NAMED_QUERY_FIND_BY_ID,query=
		"select p from PostEntity p where p.id = :"+PostEntity.NAMED_QUERY_PARAM_ID)
	})
@Entity
public class PostEntity {
	
	public static final String NAMED_QUERY_FIND_ORDERED_BY_MOD_DATE = "findByModData";
	public static final String NAMED_QUERY_FIND_BY_THREADID = "findByThreadId";
	public static final String NAMED_QUERY_PARAM_ID= "id";
	public static final String NAMED_QUERY_PARAM_THREADID = "threadId";
	public static final String NAMED_QUERY_FIND_BY_ID = "findById";
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private long created;
	private long modified;
	private String subject;
	private String content;
	private String threadId;
	@OneToOne
	private UserEntity user;
	
	public PostEntity() {
	}
	
	public PostEntity(UserEntity user, long created, long modified, String subject, String content, String threadId) {
		this.user = user;
		this.created = created;
		this.modified = modified;
		this.subject = subject;
		this.content = content;
		this.threadId = threadId;
	}
	
	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}
}

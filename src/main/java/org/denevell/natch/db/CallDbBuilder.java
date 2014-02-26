package org.denevell.natch.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;

public class CallDbBuilder<ListItem> {
	
	private EntityManager mEntityManager;
	private String mNamedQuery;
	private int mFirstResult;
	private int mMaxResults;
	private HashMap<String, Object> mQueryParams = new HashMap<String, Object>();
	
	public CallDbBuilder() {
	}

	public CallDbBuilder<ListItem> namedQuery(String nq) {
		mNamedQuery = nq;
		return this;
	}
	
	public CallDbBuilder<ListItem> start(int start) {
		mFirstResult = start;
		return this;
	}

	public CallDbBuilder<ListItem> max(int max) {
		mMaxResults = max;
		return this;
	}

	public CallDbBuilder<ListItem> queryParam(String key, Object val) {
		mQueryParams.put(key, val);
		return this;
	}
	
	public List<ListItem> list(Class<ListItem> clazz) {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager();   		

	    TypedQuery<ListItem> nq = mEntityManager.createNamedQuery(mNamedQuery, clazz);
	    for (Entry<String, Object> qp: mQueryParams.entrySet()) {
	    	nq.setParameter(qp.getKey(), qp.getValue());
		}
		if(mFirstResult<0) mFirstResult=0; if(mMaxResults<0) mMaxResults=0;
	    if(mFirstResult!=-1) {
	    	nq.setFirstResult(mFirstResult);
	    }
	    if(mMaxResults!=-1) {
	    	nq.setMaxResults(mMaxResults);
	    }
	    List<ListItem> rl = nq.getResultList();
		if(rl==null) return new ArrayList<ListItem>();
		EntityUtils.closeEntityConnection(mEntityManager);
		return rl;
		//if(limit < resultList.size() && resultList.size()>0) resultList.remove(resultList.size()-1); // For some reason we're returning two records on 1 as max results.
	}

	public ListItem single(Class<ListItem> clazz) {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager();   		

	    TypedQuery<ListItem> nq = mEntityManager.createNamedQuery(mNamedQuery, clazz);
	    for (Entry<String, Object> qp: mQueryParams.entrySet()) {
	    	nq.setParameter(qp.getKey(), qp.getValue());
		}
	    List<ListItem> rl = nq.getResultList();
	    if(rl==null || rl.size()==0) return null;
	    else return rl.get(0);
	}
	
}
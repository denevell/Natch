package org.denevell.natch.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;

public class CallDbBuilder<ListItem> {
	
	public static interface RunnableWith<ListItem> {
		public void item(ListItem item);
	}

	public static interface UpdateItem<ListItem> {
		public ListItem update(ListItem item);
	}

	public static interface DeleteOrMerge<ListItem> {
		public boolean shouldDelete(ListItem item);
	}

	public static interface NewItem<ListItem> {
		public ListItem newItem();
	}

	private EntityManager mEntityManager;
	private String mNamedQuery;
	private int mFirstResult;
	private int mMaxResults;
	private HashMap<String, Object> mQueryParams = new HashMap<String, Object>();
	private RunnableWith<ListItem> mMethodIfFirstItem;
	private String mCountNamedQueryForFirstItemMethod;
	private EntityTransaction mTransaction;
	
	public CallDbBuilder() {
	}

	/**
	 * Must be called before any thing else
	 */
	public CallDbBuilder<ListItem> startTransaction() {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager();   		
		mTransaction = mEntityManager.getTransaction();
		mTransaction.begin();
		return this;
	}
	
	public EntityManager getEntityManager() {
		return mEntityManager;
	}
	
	public CallDbBuilder<ListItem> closeEntityManager() {
		EntityUtils.closeEntityConnection(mEntityManager);
		return this;
	}
	
	public CallDbBuilder<ListItem> commitAndCloseEntityManager() {
		try {
			mTransaction.commit();
			return this;
		} catch(Exception e){
			Log.error(this.getClass(), e.getMessage(), e);
			if(mTransaction!=null && mTransaction.isActive()) mTransaction.rollback();
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			EntityUtils.closeEntityConnection(mEntityManager);
		}
	}

	public CallDbBuilder<ListItem> useTransaction(EntityManager entityManager) {
		mEntityManager = entityManager;
		mTransaction = entityManager.getTransaction();
		return this;
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
	
	public CallDbBuilder<ListItem> clearQueryParams() {
		mQueryParams.clear();
		return this;
	}
	
	public List<ListItem> list(Class<ListItem> clazz) {
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
		return rl;
		//if(limit < resultList.size() && resultList.size()>0) resultList.remove(resultList.size()-1); // For some reason we're returning two records on 1 as max results.
	}

	public ListItem single(Class<ListItem> clazz) {
		TypedQuery<ListItem> nq = mEntityManager.createNamedQuery(mNamedQuery, clazz);
		for (Entry<String, Object> qp: mQueryParams.entrySet()) {
			nq.setParameter(qp.getKey(), qp.getValue());
		}
		List<ListItem> rl = nq.getResultList();
		if(rl==null || rl.size()==0) return null;
		else return rl.get(0);
	}

	/**
	 * 
	 * @param primaryKey
	 * @param pessimisticRead
	 * @param clazz
	 * @return
	 */
	public ListItem find(Object primaryKey, boolean pessimisticRead, Class<ListItem> clazz) {
		ListItem item = null;
		if(pessimisticRead) {
			item = mEntityManager.find(clazz, primaryKey, LockModeType.PESSIMISTIC_READ);
		} else {
			item = mEntityManager.find(clazz, primaryKey);
		}
		return item;
	}
	
	/**
	 * Should take in a 'count' named query
	 * @return
	 */
	public long count() {
		Query q = (Query) mEntityManager.createNamedQuery(mNamedQuery);
		for (Entry<String, Object> entry : mQueryParams.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
	
		long countResult= (Long) q.getSingleResult();				
		return countResult;
	}

	/**
	 * Should take in a 'count' named query
	 * @return
	 */
	public boolean isFirst() {
		return count()==0;
	}

	/**
	 * @throws RuntimeException if there was an error adding
	 */
	public void add(ListItem instance) {
		mQueryParams = new HashMap<String, Object>(); // So as not to use ones for addIfDoesntExist
		if(mCountNamedQueryForFirstItemMethod!=null &&
				mMethodIfFirstItem!=null &&
				namedQuery(mCountNamedQueryForFirstItemMethod).isFirst()) {
			mMethodIfFirstItem.item(instance);
		}
		mEntityManager.persist(instance);
	}

	/**
	 * @return false if it already exists
	 * @throws RuntimeException if there was an error adding
	 */
	public boolean addIfDoesntExist(String listNamedQuery, ListItem instance) {
		namedQuery(listNamedQuery);
		if(!exists()) {
			add(instance);
			return true;
		} else {
			Log.info(getClass(), "Can't add, already exists");
			return false;
		}
	}
	
	public CallDbBuilder<ListItem> update(ListItem instance) {
		mEntityManager.merge(instance);
		return this;
	}	
	
	/**
	 * Ensure you've set query parameters before calling this
	 * @throws RuntimeException if error updating
	 * @return false if it couldn't find the item to update
	 */
	public boolean findAndUpdate(String namedQuery, RunnableWith<ListItem> runnableWith, Class<ListItem> clazz) {
		ListItem toBeUpdated  = namedQuery(namedQuery).single(clazz);
		if(toBeUpdated==null) return false;
		runnableWith.item(toBeUpdated);
		update(toBeUpdated);
		return true;
	}

	public CallDbBuilder<ListItem> findAndUpdateOrDelete(Object primaryKey, 
			DeleteOrMerge<ListItem> deleteOrMerge,
			Class<ListItem> clazz) {
		ListItem toBeUpdated  = find(primaryKey, true, clazz);
		boolean shouldDelete = deleteOrMerge.shouldDelete(toBeUpdated);
		if(shouldDelete) {
			mEntityManager.remove(toBeUpdated);
		} else {
			mEntityManager.merge(toBeUpdated);
		}
		return this;
	}
	
	/**
	 * Needs a named query set which returns a list
	 */
	public boolean exists() {
		Query q = (Query) mEntityManager.createNamedQuery(mNamedQuery);
		for (Entry<String, Object> entry : mQueryParams.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		try {
			@SuppressWarnings("rawtypes")
			List list = q.getResultList();
			if(list.size()>0) return true;
			else return false;
		} catch(NoResultException e) {
			return false;
		}
	}
	
	/**
	 * RunnableWith run in the add method.
	 * Any query parameters are cleared when this is called
	 */
	public CallDbBuilder<ListItem> ifFirstItem(String countNamedQuery, RunnableWith<ListItem> runnableWith) {
		mCountNamedQueryForFirstItemMethod = countNamedQuery;
		mMethodIfFirstItem = runnableWith;
		return this;
	}

	public ListItem createOrUpdate(
			String threadId,
			UpdateItem<ListItem> updateItem, 
			NewItem<ListItem> newItem,
			Class<ListItem> listItemClass) {
		ListItem foundItem = find(threadId, true, listItemClass);
		if(foundItem==null) {
			foundItem = newItem.newItem();
			mEntityManager.persist(foundItem);
		} else {
			foundItem = updateItem.update(foundItem);
			mEntityManager.merge(foundItem);
		}
		return foundItem;
	}

}
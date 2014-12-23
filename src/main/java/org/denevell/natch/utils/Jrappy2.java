package org.denevell.natch.utils;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.jpa.JpaQuery;


public class Jrappy2<ReturnOb> {
  
  private EntityManager mEntityManager;
  private boolean mGenericError;
  private boolean mNotFound;
  private boolean mNotAllowed;
  private ReturnOb mFoundEntity;
  private List<ReturnOb> mFoundEntities;
  
  private Jrappy2() {}

  public static Response persist(EntityManagerFactory factory, Object object) {
	  return Jrappy2
         .beginTransaction(factory)
         .persist(object)
         .commitAndClose()
         .httpReturn();
  }

  public static <T> Response update(
      EntityManagerFactory factory, 
      Class<T> clazz, 
      Object primaryKey, 
      UnaryOperator<T> updateEntity) {
	  return Jrappy2
         .beginTransaction(factory)
         .update(primaryKey, null, null, updateEntity, clazz)
         .commitAndClose()
         .httpReturn();
  }

  public static <T> Response update(
      EntityManagerFactory factory, 
      Class<T> clazz, 
      Object primaryKey, 
      Predicate<T> allowedPredicate, 
      UnaryOperator<T> updateEntity) {
	  return Jrappy2
         .beginTransaction(factory)
         .update(primaryKey, null, allowedPredicate, updateEntity, clazz)
         .commitAndClose()
         .httpReturn();
  }

  public static <ReturnOb> Response update(
      EntityManagerFactory factory, 
      Class<ReturnOb> clazz, 
      Object primaryKey, 
      Predicate<ReturnOb> extraIsFoundPredicate, 
      Predicate<ReturnOb> allowedPredicate, 
      UnaryOperator<ReturnOb> updateEntity) {
	  return Jrappy2
         .beginTransaction(factory)
         .update(primaryKey, extraIsFoundPredicate, allowedPredicate, updateEntity, clazz)
         .commitAndClose()
         .httpReturn();
  }

  public static <T> Response remove(
      EntityManagerFactory factory, 
      Object primaryKey,
      Predicate<T> prediate,
      Class<T> clazz) {
	  return Jrappy2
         .beginTransaction(factory)
         .remove(primaryKey, prediate, clazz)
         .commitAndClose()
         .httpReturn();
  }

	public static <ReturnOb> Response find(
      EntityManagerFactory factory, 
	    Object primaryKey, 
	    boolean pessimisticRead, 
	    String nullField,
	    Class<ReturnOb> clazz) throws Exception { 
	  return Jrappy2
         .begin(factory, clazz)
         .find(primaryKey, pessimisticRead, clazz)
         .nullField(nullField)
         .close()
         .httpReturn();
	}

	private <T> Jrappy2<ReturnOb> nullField(String nullField) throws Exception {
	  if(mFoundEntity!=null && nullField!=null && nullField.trim().length()>0) {
	    mFoundEntity.getClass().getField(nullField).set(mFoundEntity, Lists.newArrayList());
	  }
    return this;
  }

  public static <ReturnOb> ReturnOb findObject(
      EntityManagerFactory factory, 
	    Object primaryKey, 
	    boolean pessimisticRead, 
	    Class<ReturnOb> clazz) {
    
	  return Jrappy2
         .begin(factory, clazz)
         .find(primaryKey, pessimisticRead, clazz)
         .close()
         .returnFoundObject();
	}

	public static <T> List<T> list(
      EntityManagerFactory factory, 
      int start, int limit,
      String orderByDescAttribute,
	    boolean desc, 
	    Pair<String, String> memberOf,
	    Class<T> class1) {
    return Jrappy2.begin(JPAFactoryContextListener.sFactory, class1)
        .list(start, limit, orderByDescAttribute, desc, memberOf, class1)
        .close()
        .returnFoundObjects();
	}
	
	public static <T> long count(
      EntityManagerFactory factory, 
	    Class<T> clazz) {
	  EntityManager entityManager = JPAFactoryContextListener.sFactory.createEntityManager(); 
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		q.select(cb.count(q.from(clazz)));
		Long singleResult = entityManager.createQuery(q).getSingleResult();
		entityManager.close();
    return singleResult;
	}
	
	public <T extends ReturnOb> Jrappy2<ReturnOb> find(
	    Object primaryKey, 
	    boolean pessimisticRead, 
	    Class<T> clazz) {
		T item = null;
		if(pessimisticRead) {
			item = mEntityManager.find(clazz, primaryKey, LockModeType.PESSIMISTIC_READ);
		} else {
			item = mEntityManager.find(clazz, primaryKey);
		}
		if(item==null) {
		  mNotFound = true;
		} else {
		  mFoundEntity = item;
		}
		return this;
	}

	@FunctionalInterface
	public static interface QueryUpdate<T> {
	  public void yeah(CriteriaBuilder cb, CriteriaQuery<T> q);
	}

	@SuppressWarnings("unchecked")
  public <T extends ReturnOb> Jrappy2<ReturnOb> list(
	    int start, 
	    int limit,
	    String orderbyDescAttribute,
	    boolean desc,
	    Pair<String, String> memberOf,
	    Class<T> clazz) {

	  // WHAT ABOUT ADDING IN memberOf.getRight? and order by?
		String from = "select p from "+clazz.getSimpleName()+" p ";
		if(memberOf!=null) {
		   from = from + "where " + " :member member of " + "p."+memberOf.getRight() + " ";
		}
		if(orderbyDescAttribute!=null) {
		  if(desc) {
		    from = from + "order by " + "p."+orderbyDescAttribute  + " desc";
		  } else {
		    from = from + "order by " + "p."+orderbyDescAttribute  + " asc";
		  }
		}
		TypedQuery<T> q = mEntityManager.createQuery(from, clazz);
		if(memberOf!=null) {
		  q.setParameter("member", memberOf.getLeft());
		}
    if(start!=-1) {
      q.setFirstResult(start);
    }
    if(limit!=-1) {
      q.setMaxResults(limit);
    }

    List<T> item = q.getResultList();
		if(item==null) {
		  mNotFound = true;
		} else {
		  mFoundEntities = (List<ReturnOb>) item;
		}
		return this;
	}

  public <T> Jrappy2<ReturnOb> update(
      Object primaryKey, 
      Predicate<T> extraIsFoundPredicate, 
      Predicate<T> allowedPredicate, 
      UnaryOperator<T> updateEntity, 
      Class<T> clazz) {
    try {
      T found = mEntityManager.find(clazz, primaryKey, LockModeType.PESSIMISTIC_WRITE);
      if(found==null) {
        mNotFound = true;
      } else if(extraIsFoundPredicate!=null && !extraIsFoundPredicate.test(found)) {
        mNotFound = true;
      } else if(allowedPredicate!=null && !allowedPredicate.test(found)) {
        mNotAllowed = true;
      } else {
        found = updateEntity.apply(found);
      }
      return this;
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).info("Problem finding / updating entity", e);
      mGenericError = true;
      return this;
    }
  }

  public <T> Jrappy2<ReturnOb> remove(
      Object primaryKey, 
      Predicate<T> allowedPredicate, 
      Class<T> clazz) {
    try {
      T found = mEntityManager.find(clazz, primaryKey, LockModeType.PESSIMISTIC_WRITE);
      if(found==null) {
        mNotFound = true;
        return this;
      }
      if(allowedPredicate!=null && !allowedPredicate.test(found)) {
        mNotAllowed = true;
        return this;
      }
      mEntityManager.remove(found);
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).info("Problem persisting entity", e);
      mGenericError = true;
    }
    return this;
  }
  

  public Jrappy2<ReturnOb> persist(Object object) {
    try {
      mEntityManager.persist(object);
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).info("Problem persisting entity", e);
      mGenericError = true;
    }
    return this;
  }

  public static <ReturnOb> Jrappy2<ReturnOb> beginTransaction(EntityManagerFactory factory) {
    Jrappy2<ReturnOb> jrappy2 = new Jrappy2<>();
    jrappy2.mEntityManager = factory.createEntityManager();
    jrappy2.mEntityManager.getTransaction().begin();
    return jrappy2;
  }

  public static <ReturnOb> Jrappy2<ReturnOb> begin(EntityManagerFactory factory, Class<ReturnOb> class1) {
    Jrappy2<ReturnOb> jrappy2 = new Jrappy2<>();
    jrappy2.mEntityManager = factory.createEntityManager();
    return jrappy2;
  }

  public Jrappy2<ReturnOb> commitAndClose() {
    try {
      mEntityManager.getTransaction().commit();
      mEntityManager.close();
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).info("Error during committing transaction and closing entity manager", e);
      mGenericError = true;
    }
    return this;
  }

  public Jrappy2<ReturnOb> close() {
    try {
      mEntityManager.close();
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).info("Error during closing entity manager", e);
      mGenericError = true;
    }
    return this;
  }

  public List<ReturnOb> returnFoundObjects() {
    return mFoundEntities;
  }

  public ReturnOb returnFoundObject() {
    return mFoundEntity;
  }

  public Response httpReturn() {
    if(mNotFound) {
      return new ModelResponse<>(404, mFoundEntity).httpReturn();
    } else if(mNotAllowed) {
      return new ModelResponse<>(403, mFoundEntity).httpReturn();
    } else if(mGenericError) {
      return new ModelResponse<>(500, mFoundEntity).httpReturn();
    } else {
      return new ModelResponse<>(200, mFoundEntity).httpReturn();
    }
  }
	
}
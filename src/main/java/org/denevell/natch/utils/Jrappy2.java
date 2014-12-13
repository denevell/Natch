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
import javax.persistence.criteria.Root;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;


public class Jrappy2<ReturnOb> {
  
  private static EntityManager mEntityManager;
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

	public static <ReturnOb> Response find(
      EntityManagerFactory factory, 
	    Object primaryKey, 
	    boolean pessimisticRead, 
	    Class<ReturnOb> clazz) {
	  return Jrappy2
         .begin(factory, clazz)
         .find(primaryKey, pessimisticRead, clazz)
         .close()
         .httpReturn();
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
	    Class<T> clazz) {
	  return list(factory, start, limit, orderByDescAttribute, clazz, null);
	}

	public static <T> List<T> list(
      EntityManagerFactory factory, 
      int start, int limit,
      String orderByDescAttribute,
	    Class<T> clazz, 
	    QueryUpdate<T> whereArgs) {
    return Jrappy2.begin(JPAFactoryContextListener.sFactory, clazz)
        .list(start, limit, orderByDescAttribute, clazz, whereArgs)
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
	    Class<T> clazz, 
	    QueryUpdate<T> queryOperators) {
		CriteriaBuilder cb = mEntityManager.getCriteriaBuilder();
		CriteriaQuery<T> q = cb.createQuery(clazz);
		Root<T> c = q.from(clazz);
		if(orderbyDescAttribute!=null) {
		  q.select(c).orderBy(cb.desc(c.get(orderbyDescAttribute)));
		}
		if(queryOperators!=null) {
		  queryOperators.yeah(cb, q);
		}
    TypedQuery<T> item = mEntityManager.createQuery(q); 
    if(start!=-1) {
      item.setFirstResult(start);
    }
    if(limit!=-1) {
      item.setMaxResults(limit);
    }
		if(item==null) {
		  mNotFound = true;
		} else {
		  mFoundEntities = (List<ReturnOb>) item.getResultList();
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
      T found = mEntityManager.find(clazz, primaryKey, LockModeType.PESSIMISTIC_READ);
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
      Logger.getLogger(Jrappy2.class).debug("Problem finding / updating entity", e);
      mGenericError = true;
      return this;
    }
  }

  public Jrappy2<ReturnOb> persist(Object object) {
    try {
      mEntityManager.persist(object);
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).debug("Problem persisting entity", e);
      mGenericError = true;
    }
    return this;
  }
  
  public static <ReturnOb> Jrappy2<ReturnOb> beginTransaction(EntityManagerFactory factory) {
    Jrappy2<ReturnOb> jrappy2 = new Jrappy2<>();
    mEntityManager = factory.createEntityManager();
    mEntityManager.getTransaction().begin();
    return jrappy2;
  }

  public static <ReturnOb> Jrappy2<ReturnOb> begin(EntityManagerFactory factory, Class<ReturnOb> class1) {
    Jrappy2<ReturnOb> jrappy2 = new Jrappy2<>();
    mEntityManager = factory.createEntityManager();
    return jrappy2;
  }

  public Jrappy2<ReturnOb> commitAndClose() {
    try {
      mEntityManager.getTransaction().commit();
      mEntityManager.close();
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).debug("Error during committing transaction and closing entity manager", e);
      mGenericError = true;
    }
    return this;
  }

  public Jrappy2<ReturnOb> close() {
    try {
      mEntityManager.close();
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).debug("Error during closing entity manager", e);
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
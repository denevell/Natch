package org.denevell.natch.utils;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;


public class Jrappy2 {
  
  private static EntityManager mEntityManager;
  private boolean mGenericError;
  private boolean mNotFound;
  private boolean mNotAllowed;
  private Object mFoundEntity;
  
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

  public static <T> Response update(
      EntityManagerFactory factory, 
      Class<T> clazz, 
      Object primaryKey, 
      Predicate<T> extraIsFoundPredicate, 
      Predicate<T> allowedPredicate, 
      UnaryOperator<T> updateEntity) {
	  return Jrappy2
         .beginTransaction(factory)
         .update(primaryKey, extraIsFoundPredicate, allowedPredicate, updateEntity, clazz)
         .commitAndClose()
         .httpReturn();
  }

	public static <T> Response find(
      EntityManagerFactory factory, 
	    Object primaryKey, 
	    boolean pessimisticRead, 
	    Class<T> clazz) {
	  return Jrappy2
         .begin(factory)
         .find(primaryKey, pessimisticRead, clazz)
         .close()
         .httpReturn();
	}

	public <T> Jrappy2 find(
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

  public <T> Jrappy2 update(
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

  public Jrappy2 persist(Object object) {
    try {
      mEntityManager.persist(object);
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).debug("Problem persisting entity", e);
      mGenericError = true;
    }
    return this;
  }
  
  public static Jrappy2 beginTransaction(EntityManagerFactory factory) {
    Jrappy2 jrappy2 = new Jrappy2();
    mEntityManager = factory.createEntityManager();
    mEntityManager.getTransaction().begin();
    return jrappy2;
  }

  public static Jrappy2 begin(EntityManagerFactory factory) {
    Jrappy2 jrappy2 = new Jrappy2();
    mEntityManager = factory.createEntityManager();
    return jrappy2;
  }

  public Jrappy2 commitAndClose() {
    try {
      mEntityManager.getTransaction().commit();
      mEntityManager.close();
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).debug("Error during committing transaction and closing entity manager", e);
      mGenericError = true;
    }
    return this;
  }

  public Jrappy2 close() {
    try {
      mEntityManager.close();
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).debug("Error during closing entity manager", e);
      mGenericError = true;
    }
    return this;
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
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
  
  private Jrappy2() {}

  public static Response persist(EntityManagerFactory factory, Object object) {
	  return Jrappy2
         .beginTransaction(factory)
         .persist(object)
         .commitAndClose()
         .httpReturn();
  }

  public static <T> Response update(EntityManagerFactory factory, Class<T> clazz, Object primaryKey, UnaryOperator<T> updateEntity) {
	  return Jrappy2
         .beginTransaction(factory)
         .update(primaryKey, updateEntity, null, clazz)
         .commitAndClose()
         .httpReturn();
  }

  public static <T> Response update(EntityManagerFactory factory, Class<T> clazz, Object primaryKey, Predicate<T> predicate, UnaryOperator<T> updateEntity) {
	  return Jrappy2
         .beginTransaction(factory)
         .update(primaryKey, updateEntity, predicate, clazz)
         .commitAndClose()
         .httpReturn();
  }

  public <T> Jrappy2 update(Object primaryKey, UnaryOperator<T> updateEntity, Predicate<T> predicate, Class<T> clazz) {
    try {
      T found = mEntityManager.find(clazz, primaryKey, LockModeType.PESSIMISTIC_READ);
      if(found==null) {
        mNotFound = true;
      } else if(predicate!=null && !predicate.test(found)) {
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

  public Jrappy2 commitAndClose() {
    try {
      mEntityManager.getTransaction().commit();
      mEntityManager.close();
    } catch(Exception e) {
      Logger.getLogger(Jrappy2.class).debug("Committing transaction and closing entity manager", e);
      mGenericError = true;
    }
    return this;
  }

  public Response httpReturn() {
    if(mNotFound) {
      return new ModelResponse<Void>(404, null).httpReturn();
    } else if(mNotAllowed) {
      return new ModelResponse<Void>(403, null).httpReturn();
    } else if(mGenericError) {
      return new ModelResponse<Void>(500, null).httpReturn();
    } else {
      return new ModelResponse<Void>(200, null).httpReturn();
    }
  }
	
}
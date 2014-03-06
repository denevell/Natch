package org.denevell.natch.serv.users.toggleAdmin;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.denevell.natch.auth.LoginAuthKeysSingleton;
import org.denevell.natch.db.entities.UserEntity;
import org.denevell.natch.db.entities.UserEntityQueries;
import org.denevell.natch.utils.EntityUtils;
import org.denevell.natch.utils.JPAFactoryContextListener;
import org.denevell.natch.utils.Log;

public class UsersAdminToggleModel {
	
	private EntityManager mEntityManager;
    private UserEntityQueries mUserEntityQueries;
    private LoginAuthKeysSingleton mAuthDataGenerator;
	
	/**
	 * For DI testing
	 * @param userEntites 
	 * @param authData 
	 */
	public UsersAdminToggleModel(EntityManager entityManager, UserEntityQueries userEntites, LoginAuthKeysSingleton authData) {
		mEntityManager =  entityManager;
		mUserEntityQueries = userEntites;
		mAuthDataGenerator = authData;
	}
	
	public UsersAdminToggleModel() {
	    mUserEntityQueries = new UserEntityQueries();
        mAuthDataGenerator = LoginAuthKeysSingleton.getInstance();
	}
	
	public void init() {
		EntityManagerFactory factory = JPAFactoryContextListener.sFactory;
		mEntityManager = factory.createEntityManager();   		
	}

	public void close() {
		EntityUtils.closeEntityConnection(mEntityManager);
	}		

	/**
	 * Returns error if there is one
	 * @param userId username
	 * @return
	 */
    public String toggleAdmin(String userId) {
        EntityTransaction trans = mEntityManager.getTransaction();
        try {
            trans.begin();
            List<UserEntity> userEntities = mUserEntityQueries.getUserByUsername(userId, mEntityManager);
            UserEntity userEntity = userEntities.get(0);
            boolean admin = (userEntity.isAdmin()) ? false : true;
            userEntity.setAdmin(admin);
            UserEntity loggedInEntity = mAuthDataGenerator.getLoggedinUser(userId);
            if(loggedInEntity!=null) {
                loggedInEntity.setAdmin(admin);
            }
            mEntityManager.merge(userEntity);
            trans.commit();
            return null;
        } catch(Exception e) {
            Log.info(getClass(), "Error editing: " + e.toString());
            e.printStackTrace();
            try {
                trans.rollback();
            } catch(Exception e1) {
                Log.info(getClass(), "Error rolling back: " + e.toString());
                e1.printStackTrace();
            }
            return e.getMessage();
        }         
    }
	
}
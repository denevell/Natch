package org.denevell.natch.utils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;

public interface UserGetLoggedInService {

  User get(Object authObject);

  public static interface Username {
    String getUsername();
  }

  public static interface Admin {
    boolean getAdmin();
  }

  public static interface SystemUser extends Admin, Username {}

  public static class UserGetLoggedInModelImpl implements UserGetLoggedInService {

    private WebTarget mServiceTarget;

    public UserGetLoggedInModelImpl() {
      Client client = JerseyClientBuilder.createClient();
      client.register(JacksonFeature.class);
      String uri = ManifestVars.getUserServiceUrl();
      WebTarget target = client.target(uri);
      mServiceTarget = target.path("user").path("get");
    }

    @Override
    public User get(Object authObject) {
      try {
        User u = mServiceTarget.request().header("AuthKey", (String) authObject).get(User.class);
        return u;
      } catch (Exception e) {
        return null;
      }
    }

  }
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class User implements SystemUser {

    public String username;
    public boolean admin;
    public boolean resetPasswordRequest;
    public String recoveryEmail;
    
    public User() {
    }

    public User(String username, boolean isAdmin) {
      this.username = username;
      this.admin = isAdmin;
    }

    @Override
    public String getUsername() {
      return username;
    }

    @Override
    public boolean getAdmin() {
      return admin;
    }
  }

}

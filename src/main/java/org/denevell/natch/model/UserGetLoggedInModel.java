package org.denevell.natch.model;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.denevell.natch.utils.ManifestVars;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.jvnet.hk2.annotations.Service;

public interface UserGetLoggedInModel {

  User get(Object authObject);

  @Service
  public static class UserGetLoggedInModelImpl implements UserGetLoggedInModel {

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
  public static class User {

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
  }

}

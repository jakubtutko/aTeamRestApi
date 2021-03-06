package cz.vutbr.fit.ateam.web.dtos.user;

import cz.vutbr.fit.ateam.persistence.models.User;

import java.io.Serializable;

/**
 * DTO contains basic information about user.
 */
public class UserTokenDTO implements Serializable {
    private String authToken;
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;

    public UserTokenDTO(User user) {
        authToken = user.getAuthToken();
        id = user.getId();
        email = user.getEmail();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        avatarUrl = user.getAvatarUrl();
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

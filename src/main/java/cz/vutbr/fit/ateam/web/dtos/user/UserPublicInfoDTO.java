package cz.vutbr.fit.ateam.web.dtos.user;

import cz.vutbr.fit.ateam.persistence.models.User;

import java.io.Serializable;

/**
 * Public user's information.
 */
public class UserPublicInfoDTO implements Serializable {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;

    public UserPublicInfoDTO(User user) {
        id = user.getId();
        email = user.getEmail();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        avatarUrl = user.getAvatarUrl();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}

package cz.vutbr.fit.ateam.web.dtos.notification;

import cz.vutbr.fit.ateam.persistence.models.BaseModel;
import cz.vutbr.fit.ateam.persistence.models.User;
import cz.vutbr.fit.ateam.web.dtos.user.UserPublicInfoDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO to notification.
 */
public class NotificationDTO implements Serializable {
    private String event;
    private long createdAt;
    private UserPublicInfoDTO user;
    private String objectType;
    private Integer objectId;

    public NotificationDTO(Events event, User user, BaseModel model) {
        this.event = event.name().toLowerCase();
        this.createdAt = (new Date()).getTime();
        this.user = new UserPublicInfoDTO(user);
        this.objectType = model.getClass().getSimpleName();
        this.objectId = model.getId();
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public UserPublicInfoDTO getUser() {
        return user;
    }

    public void setUser(UserPublicInfoDTO user) {
        this.user = user;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public enum Events {
        DELETED,
        UPDATED,
        CREATED
    }
}

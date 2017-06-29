package cz.vutbr.fit.ateam.web.dtos.task;

import cz.vutbr.fit.ateam.persistence.models.Comment;
import cz.vutbr.fit.ateam.web.dtos.user.UserPublicInfoDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * Basic info about task's comment.
 */
public class TaskCommentDTO implements Serializable {
    private Integer id;
    private String description;
    private UserPublicInfoDTO author;
    private Date createdAt;
    private Date updatedAt;

    public TaskCommentDTO(Comment comment) {
        this.id = comment.getId();
        this.description = comment.getDescription();
        this.author = new UserPublicInfoDTO(comment.getAuthor());
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserPublicInfoDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserPublicInfoDTO author) {
        this.author = author;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

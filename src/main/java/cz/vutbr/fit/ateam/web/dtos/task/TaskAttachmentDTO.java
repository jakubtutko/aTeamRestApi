package cz.vutbr.fit.ateam.web.dtos.task;

import cz.vutbr.fit.ateam.persistence.models.Attachment;
import cz.vutbr.fit.ateam.web.dtos.user.UserPublicInfoDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * Basic info about attachment.
 */
public class TaskAttachmentDTO implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private UserPublicInfoDTO author;
    private String mimeType;
    private Date createdAt;
    private Date updatedAt;

    public TaskAttachmentDTO(Attachment attachment) {
        this.id = attachment.getId();
        this.name = attachment.getName();
        this.description = attachment.getDescription();
        this.author = new UserPublicInfoDTO(attachment.getAuthor());
        this.mimeType = attachment.getMimeType();
        this.createdAt = attachment.getCreatedAt();
        this.updatedAt = attachment.getUpdatedAt();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date cretedAt) {
        this.createdAt = cretedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

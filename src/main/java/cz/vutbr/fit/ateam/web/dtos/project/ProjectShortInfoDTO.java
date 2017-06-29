package cz.vutbr.fit.ateam.web.dtos.project;

import cz.vutbr.fit.ateam.persistence.models.Project;
import cz.vutbr.fit.ateam.web.dtos.user.UserPublicInfoDTO;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Short info about project.
 */
public class ProjectShortInfoDTO implements Serializable {
    private Integer id;
    private String name;
    private Integer color;
    private String description;
    private UserPublicInfoDTO author;
    private Set<UserPublicInfoDTO> sharedUsers;

    public ProjectShortInfoDTO(Project project) {
        if (project == null) {
            return;
        }
        this.id = project.getId();
        this.name = project.getName();
        this.color = project.getColor();
        this.author = new UserPublicInfoDTO(project.getAuthor());
        this.description = project.getDescription();
        this.sharedUsers = Collections.EMPTY_SET;
        if (project.getSharedUsers() != null) {
            this.sharedUsers = project.getSharedUsers().stream().map(UserPublicInfoDTO::new).collect(Collectors.toSet());
        }
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

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public UserPublicInfoDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserPublicInfoDTO author) {
        this.author = author;
    }

    public Set<UserPublicInfoDTO> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(Set<UserPublicInfoDTO> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

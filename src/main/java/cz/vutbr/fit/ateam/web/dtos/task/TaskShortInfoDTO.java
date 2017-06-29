package cz.vutbr.fit.ateam.web.dtos.task;

import cz.vutbr.fit.ateam.persistence.models.Task;
import cz.vutbr.fit.ateam.web.dtos.label.LabelDTO;
import cz.vutbr.fit.ateam.web.dtos.project.ProjectShortInfoDTO;
import cz.vutbr.fit.ateam.web.dtos.user.UserPublicInfoDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic info about task.
 */
public class TaskShortInfoDTO implements Serializable {
    private Integer id;
    private Date createdAt;
    private Date updatedAt;
    private Date dueDate;
    private String name;
    private String priority;
    private String state;
    private UserPublicInfoDTO owner;
    private List<UserPublicInfoDTO> assignedUsers;
    private List<LabelDTO> labels;
    private Integer parentTaskId;
    private ProjectShortInfoDTO project;

    public TaskShortInfoDTO(Task task) {
        if (task == null) {
            return;
        }

        this.id = task.getId();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getCreatedAt();
        this.dueDate = task.getDueDate();
        this.name = task.getName();
        this.priority = task.getPriority().toString();
        this.state = task.getState().toString();
        this.owner = new UserPublicInfoDTO(task.getOwner());
        this.project = new ProjectShortInfoDTO(task.getProject());

        if (task.getParentTask() == null) {
            this.parentTaskId = null;
        } else {
            this.parentTaskId = task.getParentTask().getId();
        }

        this.assignedUsers = new ArrayList<>();
        if (task.getAssignedUsers() != null)
            assignedUsers.addAll(task.getAssignedUsers().stream().map(UserPublicInfoDTO::new).collect(Collectors.toList()));

        this.labels = new ArrayList<>();
        if (task.getLabels() != null)
            labels.addAll(task.getLabels().stream().map(LabelDTO::new).collect(Collectors.toList()));
    }

    public static int compareByDueDate(TaskShortInfoDTO o1, TaskShortInfoDTO o2) {
        if (o1.getDueDate() == o2.getDueDate()) {
            return 0;
        } else if (o1.getDueDate() == null) {
            return 1;
        } else if (o2.getDueDate() == null) {
            return -1;
        } else {
            return o1.getDueDate().compareTo(o2.getDueDate());
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setId(int Integer) {
        this.id = id;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UserPublicInfoDTO getOwner() {
        return owner;
    }

    public void setOwner(UserPublicInfoDTO owner) {
        this.owner = owner;
    }

    public List<UserPublicInfoDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<UserPublicInfoDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public List<LabelDTO> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelDTO> labels) {
        this.labels = labels;
    }

    public Integer getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Integer parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public ProjectShortInfoDTO getProject() {
        return project;
    }

    public void setProject(ProjectShortInfoDTO project) {
        this.project = project;
    }
}

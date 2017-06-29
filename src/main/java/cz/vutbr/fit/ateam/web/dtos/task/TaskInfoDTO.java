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
 * Complete information about task.
 */
public class TaskInfoDTO implements Serializable {
    private Integer id;
    private String name;
    private String priority;
    private short progress;
    private String state;
    private long workedTime;
    private long expectedHours;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private Date dueDate;
    private UserPublicInfoDTO owner;
    private TaskShortInfoDTO parentTask;
    private List<TaskCommentDTO> comments;
    private ProjectShortInfoDTO project;
    private List<UserPublicInfoDTO> assignedUsers;
    private List<TaskShortInfoDTO> subTasks;
    private List<LabelDTO> labels;

    public TaskInfoDTO(Task task) {
        this.id = task.getId();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
        this.dueDate = task.getDueDate();
        this.name = task.getName();
        this.priority = task.getPriority().toString().toLowerCase();
        this.progress = task.getProgress();
        this.state = task.getState().toString().toLowerCase();
        this.workedTime = task.getWorkedTime();
        this.description = task.getDescription();
        this.expectedHours = task.getExpectedTime();
        this.owner = new UserPublicInfoDTO(task.getOwner());

        if (task.getProject() != null) {
            this.project = new ProjectShortInfoDTO(task.getProject());
        }

        if (task.getParentTask() == null) {
            this.parentTask = null;
        } else {
            this.parentTask = new TaskShortInfoDTO(task.getParentTask());
        }

        this.assignedUsers = new ArrayList<>();
        if (task.getAssignedUsers() != null)
            assignedUsers.addAll(task.getAssignedUsers().stream().map(UserPublicInfoDTO::new).collect(Collectors.toList()));

        this.subTasks = new ArrayList<>();
        if (task.getSubTasks() != null)
            subTasks.addAll(task.getSubTasks().stream()
                    .filter(t -> !t.equals(task))
                    .map(TaskShortInfoWithSubtasksDTO::new)
                    .sorted((o1, o2) -> o2.getDueDate().compareTo(o1.getDueDate()))
                    .collect(Collectors.toList()));

        this.labels = new ArrayList<>();
        if (task.getLabels() != null)
            labels.addAll(task.getLabels().stream().map(LabelDTO::new).collect(Collectors.toList()));

        this.comments = new ArrayList<>();
        if (task.getComments() != null)
            comments.addAll(task.getComments().stream().map(TaskCommentDTO::new).collect(Collectors.toList()));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public short getProgress() {
        return progress;
    }

    public void setProgress(short progress) {
        this.progress = progress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getWorkedTime() {
        return workedTime;
    }

    public void setWorkedTime(long workedTime) {
        this.workedTime = workedTime;
    }

    public UserPublicInfoDTO getOwner() {
        return owner;
    }

    public void setOwner(UserPublicInfoDTO owner) {
        this.owner = owner;
    }

    public TaskShortInfoDTO getParentTask() {
        return parentTask;
    }

    public void setParentTask(TaskShortInfoDTO parentTask) {
        this.parentTask = parentTask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExpectedHours() {
        return expectedHours;
    }

    public void setExpectedHours(long expectedHours) {
        this.expectedHours = expectedHours;
    }

    public ProjectShortInfoDTO getProject() {
        return project;
    }

    public void setProject(ProjectShortInfoDTO project) {
        this.project = project;
    }

    public List<UserPublicInfoDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<UserPublicInfoDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public List<TaskShortInfoDTO> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<TaskShortInfoDTO> subTasks) {
        this.subTasks = subTasks;
    }

    public List<LabelDTO> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelDTO> labels) {
        this.labels = labels;
    }

    public List<TaskCommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<TaskCommentDTO> comments) {
        this.comments = comments;
    }
}

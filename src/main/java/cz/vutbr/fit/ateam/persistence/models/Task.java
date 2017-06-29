package cz.vutbr.fit.ateam.persistence.models;

import org.hibernate.Session;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "tasks")
public class Task extends BaseModel {

    private String name;
    private State state;
    private Date dueDate;
    private long workedTime;
    private long expectedTime;
    private Priority priority;
    private short progress;
    private Set<User> assignedUsers;
    private User owner;
    private Set<Comment> comments;
    private Set<Checklist> checklists;
    private Set<Label> labels;
    private Set<Attachment> attachments;
    private Task parentTask;
    private Project project;
    private Set<Task> subTasks;
    private String description;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "state")
    @Enumerated(EnumType.ORDINAL)
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Column(name = "worked_time")
    public long getWorkedTime() {
        return workedTime;
    }

    public void setWorkedTime(long workedTime) {
        this.workedTime = workedTime;
    }

    @Column(name = "expected_hours")
    public long getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(long expectedTime) {
        this.expectedTime = expectedTime;
    }

    @Column(name = "priority")
    @Enumerated(EnumType.ORDINAL)
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Column(name = "progress")
    public short getProgress() {
        return progress;
    }

    public void setProgress(short progress) {
        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;
        this.progress = progress;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_tasks", joinColumns =
    @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    public Set<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public void setAssignedUsers(Session session, int[] usersId) {
        Set<User> assignedUsers = new HashSet<>();
        if (usersId != null) {
            for (int userId : usersId) {
                User assignedUser = session.get(User.class, userId);
                if (assignedUser == null) continue;
                if (project != null && !project.userHasPermissionsToManage(assignedUser)) continue;
                assignedUsers.add(assignedUser);
            }
            this.setAssignedUsers(assignedUsers);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    public Set<Checklist> getChecklists() {
        return checklists;
    }

    public void setChecklists(Set<Checklist> checklists) {
        this.checklists = checklists;
    }

    @ManyToMany
    @JoinTable(name = "tasks_labels", joinColumns =
    @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "label_id"))
    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }

    public void setLabels(Session session, String[] labels) {
        Set<Label> assignedLabels = new HashSet<>();

        for (String label : labels) {
            Label assignedLabel = Label.getOrCreateNew(session, label);
            assignedLabels.add(assignedLabel);
        }

        this.setLabels(assignedLabels);
    }

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    @ManyToOne
    @JoinColumn(name = "parent_task")
    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.REMOVE)
    public Set<Task> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(Set<Task> tasks) {
        this.subTasks = tasks;
    }

    @Column
    public String getDescription() {
        return description;
    }

    public void setDescription(String desctiption) {
        this.description = desctiption;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;

        if (this.getSubTasks() != null) {
            for (Task subTask : this.getSubTasks()) {
                subTask.setProject(project);
            }
        }
    }

    public boolean userHasPermissionsToManage(User user) {
        // return this.getAssignedUsers().contains(user) || this.getOwner().equals(user);
        if (this.getProject() != null) {
            return this.getProject().userHasPermissionsToManage(user);
        } else {
            return this.getAssignedUsers().contains(user) || this.getOwner().equals(user);
        }
    }

    public boolean canBeMovedIntoProject(Project newProject) {
        if (!newProject.userHasPermissionsToManage(this.getOwner())) {
            return false;
        }

        if (this.getAssignedUsers() != null) {
            for (User assignedUser : this.getAssignedUsers()) {
                if (!newProject.userHasPermissionsToManage(assignedUser)) {
                    return false;
                }
            }
        }

        if (this.getSubTasks() != null) {
            for (Task subTask : this.getSubTasks()) {
                if (!subTask.canBeMovedIntoProject(newProject)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public List<String> acquireUsersGcms() {
        if (this.getProject() != null) {
            return this.getProject().acquireUsersGcms();
        }

        List<String> gcms = this.getOwner().getGcms().stream().map(Gcm::getGcm).collect(Collectors.toList());

        if (this.getAssignedUsers() != null) {
            for (User assignedUser : this.getAssignedUsers()) {
                gcms.addAll(assignedUser.getGcms().stream().map(Gcm::getGcm).collect(Collectors.toList()));
            }
        }

        return gcms;
    }

    public enum State {
        NEW, ASSIGNED, IN_PROGRESS, DONE, TESTED;
    }

    public enum Priority {
        MINOR, MAJOR, CRITICAL, BLOCKER;
    }
}

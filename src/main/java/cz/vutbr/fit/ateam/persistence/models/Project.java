package cz.vutbr.fit.ateam.persistence.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "projects")
public class Project extends BaseModel {
    private String name;
    private String description;
    private int color;
    private User author;
    private Set<User> sharedUsers;
    private Set<Task> tasks;

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "color")
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_users", joinColumns =
    @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    public Set<User> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(Set<User> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public boolean userHasPermissionsToManage(User user) {
        return this.getSharedUsers().contains(user) || this.getAuthor().equals(user);
    }

    public boolean canUpdate(User user) {
        return getAuthor().equals(user);
    }

    public boolean canDelete(User user) {
        return getAuthor().equals(user);
    }

    @Override
    public List<String> acquireUsersGcms() {
        List<String> gcms = this.getAuthor().getGcms().stream().map(Gcm::getGcm).collect(Collectors.toList());

        if (this.getSharedUsers() != null) {
            for (User assignedUser : this.getSharedUsers()) {
                gcms.addAll(assignedUser.getGcms().stream().map(Gcm::getGcm).collect(Collectors.toList()));
            }
        }

        return gcms;
    }
}

package cz.vutbr.fit.ateam.persistence.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends BaseModel {

    private String authToken;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Set<Project> sharedProjects;
    private Set<Task> tasks;
    private Set<Task> assigned;
    private Set<Project> authorProjects;
    private Set<Gcm> gcms;

    @Column(name = "auth_token", unique = true)
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Column(name = "email", unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "avatar_url")
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sharedUsers")
    public Set<Project> getSharedProjects() {
        return sharedProjects;
    }

    public void setSharedProjects(Set<Project> sharedProjects) {
        this.sharedProjects = sharedProjects;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_tasks", joinColumns =
    @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "task_id"))
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    @OneToMany(mappedBy = "owner")
    public Set<Task> getAssigned() {
        return this.assigned;
    }

    public void setAssigned(Set<Task> assigned) {
        this.assigned = assigned;
    }

    @OneToMany(mappedBy = "author")
    public Set<Project> getAuthorProjects() {
        return authorProjects;
    }

    public void setAuthorProjects(Set<Project> authorProjects) {
        this.authorProjects = authorProjects;
    }

    @OneToMany(mappedBy = "user")
    public Set<Gcm> getGcms() {
        return gcms;
    }

    public void setGcms(Set<Gcm> gcms) {
        this.gcms = gcms;
    }
}

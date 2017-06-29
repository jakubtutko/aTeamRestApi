package cz.vutbr.fit.ateam.persistence.models;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "labels")
public class Label extends BaseModel {
    private String name;
    private Set<Task> tasks;

    public static Label getOrCreateNew(Session session, String name) {
        Label label = (Label) session.createCriteria(Label.class).add(Restrictions.eq("name", name)).uniqueResult();
        if (label == null) {
            label = new Label();
            label.setCreatedAt();
            label.setName(name);
            session.save(label);
        }

        return label;
    }

    @Column(name = "name", unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany
    @JoinTable(name = "tasks_labels", joinColumns =
    @JoinColumn(name = "label_id"), inverseJoinColumns = @JoinColumn(name = "task_id"))
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}

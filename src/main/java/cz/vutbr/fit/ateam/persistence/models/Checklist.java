package cz.vutbr.fit.ateam.persistence.models;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "checklists")
public class Checklist extends BaseModel {
    private String name;
    private Set<ChecklistItem> checklistItems;
    private Task task;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "checklist")
    @Cascade(CascadeType.ALL)
    public Set<ChecklistItem> getChecklistItems() {
        return checklistItems;
    }

    public void setChecklistItems(Set<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean userHasPermissionsToManage(User user) {
        return getTask().userHasPermissionsToManage(user);
    }

    @Override
    public List<String> acquireUsersGcms() {
        return this.getTask().acquireUsersGcms();
    }
}

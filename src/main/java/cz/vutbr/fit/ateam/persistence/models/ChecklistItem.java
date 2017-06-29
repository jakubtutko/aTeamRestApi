package cz.vutbr.fit.ateam.persistence.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "checklist_items")
public class ChecklistItem extends BaseModel {
    private String description;
    private Checklist checklist;
    private boolean isDone;

    @Column(name = "description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "is_done")
    public boolean isDone() {
        return isDone;
    }
    public void setDone(boolean done) {
        isDone = done;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    public Checklist getChecklist() {
        return checklist;
    }
    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public boolean userHasPermissionsToManage(User user) {
        return getChecklist().userHasPermissionsToManage(user);
    }

    @Override
    public List<String> acquireUsersGcms() {
        return this.getChecklist().acquireUsersGcms();
    }
}

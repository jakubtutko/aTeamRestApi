package cz.vutbr.fit.ateam.web.dtos.checklist;


import cz.vutbr.fit.ateam.persistence.models.ChecklistItem;

public class ChecklistItemDTO {
    private int id;
    private String description;
    private Boolean isDone;

    public ChecklistItemDTO(ChecklistItem item) {
        this.id = item.getId();
        this.description = item.getDescription();
        this.isDone = item.isDone();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

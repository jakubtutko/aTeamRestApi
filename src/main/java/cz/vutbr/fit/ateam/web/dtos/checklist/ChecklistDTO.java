package cz.vutbr.fit.ateam.web.dtos.checklist;


import cz.vutbr.fit.ateam.persistence.models.Checklist;

import java.util.List;
import java.util.stream.Collectors;

public class ChecklistDTO {
    private int id;
    private String name;
    private List<ChecklistItemDTO> items;

    public ChecklistDTO(Checklist checklist) {
        this.id = checklist.getId();
        this.name = checklist.getName();
        this.items = checklist.getChecklistItems().stream().map(ChecklistItemDTO::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChecklistItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ChecklistItemDTO> items) {
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

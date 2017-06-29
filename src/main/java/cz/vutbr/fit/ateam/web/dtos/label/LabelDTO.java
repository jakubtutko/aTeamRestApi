package cz.vutbr.fit.ateam.web.dtos.label;

import cz.vutbr.fit.ateam.persistence.models.Label;

import java.io.Serializable;

/**
 * Basic info about label.
 */
public class LabelDTO implements Serializable {
    private int id;
    private String name;

    public LabelDTO(Label label) {
        this.id = label.getId();
        this.name = label.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

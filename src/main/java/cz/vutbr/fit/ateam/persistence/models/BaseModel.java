package cz.vutbr.fit.ateam.persistence.models;


import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@MappedSuperclass
public class BaseModel extends EmptyInterceptor implements Serializable {
    protected int id;
    protected Date createdAt;
    protected Date updatedAt;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedAt() { return createdAt;}

    public void setCreatedAt(Date createdAt) {this.createdAt = createdAt;}

    public void setCreatedAt() {
        Date currentDate = new Date();
        setCreatedAt(currentDate);
        setUpdatedAt(currentDate);
    }

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public void setUpdatedAt() { setUpdatedAt(new Date()); }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        if (entity instanceof Attachment) {
            try {
                if (!(new File(((Attachment) entity).getFilePath())).delete()) {
                    System.err.println("ERROR: Cannot delete file [" + ((Attachment) entity).getFilePath() + "]!");
                }
            } catch (Exception e) {
                System.err.println("ERROR: Cannot get file! [" + e.getMessage() + "]!");
            }
        }

        super.onDelete(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        if (entity instanceof BaseModel) {
            BaseModel model = (BaseModel) entity;
            if (model.getCreatedAt() == null) {
                model.setCreatedAt();
            } else {
                model.setUpdatedAt();
            }
        }

        return super.onSave(entity, id, state, propertyNames, types);
    }

    public List<String> acquireUsersGcms() {
        return null;
    }
}

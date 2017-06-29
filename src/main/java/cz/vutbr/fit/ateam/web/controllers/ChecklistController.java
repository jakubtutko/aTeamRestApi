package cz.vutbr.fit.ateam.web.controllers;


import cz.vutbr.fit.ateam.persistence.HibernateUtil;
import cz.vutbr.fit.ateam.persistence.models.Checklist;
import cz.vutbr.fit.ateam.persistence.models.ChecklistItem;
import cz.vutbr.fit.ateam.persistence.models.User;
import cz.vutbr.fit.ateam.web.auth.Auth;
import cz.vutbr.fit.ateam.web.commons.Response;
import cz.vutbr.fit.ateam.web.dtos.checklist.ChecklistItemDTO;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods for managing checklists.
 */
public class ChecklistController {

    /**
     * Add item to checklist.
     */
    public static Response addItemToChecklist(String token, int checklistId, List<String> descriptions) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Checklist checklist = session.get(Checklist.class, checklistId);
            if (checklist == null) {
                return Response.missingRecord();
            }
            if (!checklist.userHasPermissionsToManage(user)) {
                return Response.actionDenied();
            }
            List<ChecklistItemDTO> added = new ArrayList<>();
            descriptions.stream()
                    .forEach(description -> {
                        ChecklistItem item = new ChecklistItem();
                        item.setDescription(description);
                        item.setDone(false);
                        item.setCreatedAt();
                        item.setChecklist(checklist);
                        checklist.getChecklistItems().add(item);
                        session.save(item);
                        added.add(new ChecklistItemDTO(item));

                    });
            session.save(checklist);
            tx.commit();
            return Response.ok(added);
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Delete checklist.
     */
    public static Response deleteChecklist(String token, int checklistId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Checklist checklist = session.get(Checklist.class, checklistId);
            if (checklist == null) {
                return Response.missingRecord();
            }
            if (!checklist.userHasPermissionsToManage(user)) {
                return Response.actionDenied();
            }
            session.delete(checklist);
            tx.commit();
            return Response.ok();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Edit checklist item.
     */
    public static Response editChecklistItem(int itemId, String token, String description, Boolean isDone) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            ChecklistItem item = session.get(ChecklistItem.class, itemId);
            if (item == null) {
                return Response.missingRecord();
            }
            if (!item.userHasPermissionsToManage(user)) {
                return Response.actionDenied();
            }
            if (description != null) {
                item.setDescription(description);
            }
            if (isDone != null) {
                item.setDone(isDone);
            }
            session.save(item);
            tx.commit();
            return Response.ok();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Delete checklist item.
     */
    public static Response deleteChecklistItem(int itemId, String token) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            ChecklistItem item = session.get(ChecklistItem.class, itemId);
            if (item == null) {
                return Response.missingRecord();
            }
            if (!item.userHasPermissionsToManage(user)) {
                return Response.actionDenied();
            }
            session.delete(item);
            tx.commit();
            return Response.ok();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }
}

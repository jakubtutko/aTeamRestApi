package cz.vutbr.fit.ateam.web.controllers;

import cz.vutbr.fit.ateam.persistence.HibernateUtil;
import cz.vutbr.fit.ateam.persistence.models.Gcm;
import cz.vutbr.fit.ateam.persistence.models.Task;
import cz.vutbr.fit.ateam.persistence.models.User;
import cz.vutbr.fit.ateam.web.auth.Auth;
import cz.vutbr.fit.ateam.web.commons.Response;
import cz.vutbr.fit.ateam.web.dtos.task.TaskShortInfoDTO;
import cz.vutbr.fit.ateam.web.dtos.task.TaskShortInfoWithSubtasksDTO;
import cz.vutbr.fit.ateam.web.dtos.user.UserTokenDTO;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Methods for managing user's
 */
public class UserController {

    public static Response register(String email, String password, String gcm, String firstName, String lastName) {
        if (email == null || password == null) {
            return Response.paramMissing("Parameter missing!");
        }

        Transaction tx = null;
        Response response;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            Criteria cr = session.createCriteria(User.class);
            if (cr.add(Restrictions.eq("email", email)).uniqueResult() != null) {
                return Response.recordExists();
            }

            User user = new User();
            user.setEmail(email);
            user.setPassword(Auth.createHash(password));
            user.setAuthToken(Auth.generateAuthToken(session));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            session.save(user);

            if (gcm != null) {
                Gcm gcmModel;
                List result = session.createCriteria(Gcm.class).add(Restrictions.eq("gcm", gcm)).list();

                if (result.size() == 0) {
                    gcmModel = new Gcm();
                    gcmModel.setGcm(gcm);
                } else {
                    gcmModel = (Gcm) result.get(0);
                }

                gcmModel.setUser(user);
                session.save(gcmModel);
            }

            response = Response.ok(new UserTokenDTO(user));

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            response = Response.hibernateException(e.getMessage());
        }
        return response;
    }

    public static Response login(String email, String password, String gcm) {
        try (Session session = HibernateUtil.getSession()) {
            User user = (User) session.createCriteria(User.class)
                    .add(Restrictions.eq("email", email))
                    .uniqueResult();

            if (user == null) {
                return Response.userNotFound();
            }
            if (!Auth.compareHash(password, user.getPassword())) {
                return Response.authError();
            }
            return Response.ok(new UserTokenDTO(user));

        } catch (HibernateException e) {
            return Response.hibernateException(e.getMessage());
        }
    }

    /**
     * Updates user info.
     *
     * @param token user's auth token
     * @return Response
     */
    public static Response update(
            String token, String email, String password, String gcm, String firstName, String lastName) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }
            tx = session.beginTransaction();

            if (email != null) {
                user.setEmail(email);
            }
            if (password != null) {
                user.setPassword(Auth.createHash(password));
            }
            if (lastName != null) {
                user.setFirstName(firstName);
            }
            if (lastName != null) {
                user.setLastName(lastName);
            }

            session.save(user);

            if (gcm != null) {
                Gcm gcmModel = (Gcm) session.createCriteria(Gcm.class).add(Restrictions.eq("gcm", gcm)).uniqueResult();

                if (gcmModel == null) {
                    gcmModel = new Gcm();
                    gcmModel.setGcm(gcm);
                }

                gcmModel.setUser(user);
                session.save(gcmModel);
            }

            tx.commit();
            return Response.ok(new UserTokenDTO(user));
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage());
        }
    }

    /**
     * Returns all user's tasks.
     *
     * @param token user's auth token
     * @param untilTimestamp only tasks till this timestamp will be returned
     * @return Reponse
     */
    public static Response geTasks(String token, Long untilTimestamp) {
        Date until = untilTimestamp == null ? null : new Date(untilTimestamp);
        try (Session session = HibernateUtil.getSession()) {

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Set<Task> tasks = user.getAssigned();
            tasks.addAll(user.getTasks());

            return Response.ok(tasks.stream()
                    .map(TaskShortInfoWithSubtasksDTO::new)
                    .filter(task -> {
                        if (task.getDueDate() == null) {
                            return until == null;
                        }
                        return until == null || task.getDueDate().after(new Date(0)) && task.getDueDate().before(new Date(untilTimestamp + 1));
                    })
                    .sorted(TaskShortInfoDTO::compareByDueDate)
                    .collect(Collectors.toList()));
        } catch (HibernateException e) {
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Checks if given token is valid.
     *
     * @param token auth token
     * @return Response
     */
    public static Response ping(String token) {
        try (Session session = HibernateUtil.getSession()) {

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }
            return Response.ok();
        } catch (HibernateException e) {
            return Response.hibernateException(e.getMessage(), null);
        }

    }

    /**
     * Sets new user's gcm.
     *
     * @param token user's token
     * @param gcm   user's gcm
     * @return Response
     */
    public static Response setUserGcm(String token, String gcm) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Gcm gcmModel;
            List result = session.createCriteria(Gcm.class).add(Restrictions.eq("gcm", gcm)).list();

            if (result.size() == 0) {
                gcmModel = new Gcm();
                gcmModel.setGcm(gcm);
            } else {
                gcmModel = (Gcm) result.get(0);
            }

            gcmModel.setUser(user);
            session.save(gcmModel);

            tx.commit();

            return Response.ok("Gcm's changed!");
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage());
        }
    }
}

package cz.vutbr.fit.ateam.web.controllers;


import cz.vutbr.fit.ateam.persistence.HibernateUtil;
import cz.vutbr.fit.ateam.persistence.models.Project;
import cz.vutbr.fit.ateam.persistence.models.Task;
import cz.vutbr.fit.ateam.persistence.models.User;
import cz.vutbr.fit.ateam.web.auth.Auth;
import cz.vutbr.fit.ateam.web.commons.NotificationService;
import cz.vutbr.fit.ateam.web.commons.Response;
import cz.vutbr.fit.ateam.web.dtos.project.ProjectShortInfoDTO;
import cz.vutbr.fit.ateam.web.dtos.task.TaskShortInfoDTO;
import cz.vutbr.fit.ateam.web.dtos.task.TaskShortInfoWithSubtasksDTO;
import cz.vutbr.fit.ateam.web.dtos.user.UserPublicInfoDTO;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains methods for managing projects.
 */
public class ProjectController {

    /**
     * Get all projects created by user
     */
    public static Response getCreatedProjects(String token) {
        try (Session session = HibernateUtil.getSession()) {

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            List<Project> projects = session.createCriteria(Project.class)
                    .add(Restrictions.eq("author", user)
                    ).list();

            List projectsDTO = projects.stream()
                    .map(ProjectShortInfoDTO::new)
                    .collect(Collectors.toList());

            return Response.ok(projectsDTO);


        } catch (HibernateException e) {
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Get all project relevant to user.
     */
    public static Response getAll(String token) {
        try (Session session = HibernateUtil.getSession()) {

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            List<Project> participated = session.createCriteria(Project.class)
                    .createAlias("sharedUsers", "user", JoinType.INNER_JOIN)
                    .add(Restrictions.eqOrIsNull("user.id", user.getId()))
                    .list();

            List<Project> created = session.createCriteria(Project.class)
                    .add(Restrictions.eqOrIsNull("author", user))
                    .list();


            HashSet<Project> projects = new HashSet<>(participated);
            projects.addAll(created);

            List projectsDTO = projects.stream()
                    .map(ProjectShortInfoDTO::new)
                    .sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
                    .collect(Collectors.toList());

            return Response.ok(projectsDTO);


        } catch (HibernateException e) {
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Get all projects user participate
     */
    public static Response getParticipatedProjects(String token) {
        try (Session session = HibernateUtil.getSession()) {

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            List<Project> projects = session.createCriteria(Project.class)
                    .createAlias("sharedUsers", "user")
                    .add(Restrictions.eq("user.id", user.getId()))
                    .list();

            System.out.println(projects.get(0).getSharedUsers().size());

            List projectsDTO = projects.stream()
                    .map(ProjectShortInfoDTO::new)
                    .collect(Collectors.toList());

            return Response.ok(projectsDTO);


        } catch (HibernateException e) {
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Create new project.
     */
    public static Response createProject(String token, String name, String description, Integer color) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Project project = new Project();
            project.setName(name);
            project.setAuthor(user);
            if (description != null) {
                project.setDescription(description);
            }
            if (color != null) {
                project.setColor(color);
            }

            session.save(project);
            tx.commit();

            NotificationService.notifyCreated(user, project);

            return Response.ok(new ProjectShortInfoDTO(project));

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Delete project.
     */
    public static Response delete(String token, int projectId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Project project = session.get(Project.class, projectId);
            if (project == null) {
                return Response.missingRecord();
            }
            if (project.getAuthor().equals(user)) {
                session.delete(project);
                tx.commit();
                NotificationService.notifyDeleted(user, project);
                return Response.ok();
            } else if (project.getSharedUsers().contains(user)) {
                project.getSharedUsers().remove(user);
                session.save(project);
                tx.commit();
                NotificationService.notifyUpdated(user, project);
                return Response.ok();
            } else {
                return Response.actionDenied();
            }
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Update project.
     */
    public static Response update(String token, int projectId, String name, String description, Integer color) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Project project = session.get(Project.class, projectId);
            if (project == null) {
                return Response.missingRecord();
            }
            if (!project.canUpdate(user)) {
                return Response.actionDenied();
            }
            if (name != null) {
                project.setName(name);
            }
            if (description != null) {
                project.setDescription(description);
            }
            if (color != null) {
                project.setColor(color);
            }
            session.save(project);

            tx.commit();
            NotificationService.notifyUpdated(user, project);
            return Response.ok(new ProjectShortInfoDTO(project));
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Add users to project.
     */
    public static Response addUsers(int projectId, String token, List<String> userEmails) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Project project = session.get(Project.class, projectId);
            if (project == null) {
                return Response.missingRecord();
            }
            if (!project.canUpdate(user)) {
                return Response.actionDenied();
            }
            List<User> usersToAdd = session.createCriteria(User.class).add(Restrictions.in("email", userEmails)).list();
            project.getSharedUsers().addAll(usersToAdd);

            session.save(project);
            tx.commit();
            NotificationService.notifyUpdated(user, project);
            return Response.ok(usersToAdd.stream().map(UserPublicInfoDTO::new).collect(Collectors.toList()));
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Get assigned users to project.
     */
    public static Response getUsers(String token, int projectId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Project project = session.get(Project.class, projectId);
            if (project == null) {
                return Response.missingRecord();
            }

            List<User> users = session.createCriteria(User.class)
                    .createAlias("sharedProjects", "project")
                    .add(Restrictions.eq("project.id", project.getId())).list();

            List<UserPublicInfoDTO> projectsDTO = users.stream()
                    .map(UserPublicInfoDTO::new)
                    .collect(Collectors.toList());

            return Response.ok(projectsDTO);
        } catch (HibernateException e) {
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Delete assigned iser from project.
     */
    public static Response deleteUser(String token, int projectId, String userEmail) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Project project = session.get(Project.class, projectId);
            if (project == null) {
                return Response.missingRecord("Project not found!");
            }
            if (!project.canUpdate(user)) {
                return Response.actionDenied();
            }
            User toDelete = (User) session.createCriteria(User.class).add(Restrictions.eq("email", userEmail)).uniqueResult();
            if (toDelete == null) {
                return Response.missingRecord("User not found!");
            }
            project.getSharedUsers().remove(toDelete);
            session.save(project);
            tx.commit();
            NotificationService.notifyUpdated(user, project);
            return Response.ok();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Get info about project.
     */
    public static Response getProject(String token, int projectId) {
        try (Session session = HibernateUtil.getSession()) {

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Project project = session.get(Project.class, projectId);
            if (project == null) {
                return Response.missingRecord("Project not found!");
            }

            if (!project.userHasPermissionsToManage(user)) {
                return Response.actionDenied();
            }

            return Response.ok(new ProjectShortInfoDTO(project));
        } catch (HibernateException e) {
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Get project's tasks.
     */
    public static Response getProjectTasks(String token, int projectId, Long untilTimestamp) {
        Date until = untilTimestamp == null ? null : new Date(untilTimestamp);
        try (Session session = HibernateUtil.getSession()) {

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Project project = session.get(Project.class, projectId);
            if (project == null) {
                return Response.missingRecord("Project not found!");
            }
            if (!project.userHasPermissionsToManage(user)) {
                return Response.actionDenied();
            }
            List list;
            if (until == null) {
                list = new ArrayList<>(project.getTasks());
            } else {
                list = session.createCriteria(Task.class)
                        .add(Restrictions.and(
                                Restrictions.eq("project", project),
                                Restrictions.between("dueDate", new Date(0), until))
                        ).list();
            }
            List<Task> tasks = list;

            return Response.ok(tasks.stream()
                    .map(TaskShortInfoWithSubtasksDTO::new)
                    .sorted(TaskShortInfoDTO::compareByDueDate)
                    .collect(Collectors.toList()));
        } catch (HibernateException e) {
            return Response.hibernateException(e.getMessage(), null);
        }
    }
}

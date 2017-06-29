package cz.vutbr.fit.ateam.web.controllers;

import cz.vutbr.fit.ateam.persistence.HibernateUtil;
import cz.vutbr.fit.ateam.persistence.models.Attachment;
import cz.vutbr.fit.ateam.persistence.models.Checklist;
import cz.vutbr.fit.ateam.persistence.models.ChecklistItem;
import cz.vutbr.fit.ateam.persistence.models.Comment;
import cz.vutbr.fit.ateam.persistence.models.Label;
import cz.vutbr.fit.ateam.persistence.models.Project;
import cz.vutbr.fit.ateam.persistence.models.Task;
import cz.vutbr.fit.ateam.persistence.models.User;
import cz.vutbr.fit.ateam.web.auth.Auth;
import cz.vutbr.fit.ateam.web.commons.NotificationService;
import cz.vutbr.fit.ateam.web.commons.Response;
import cz.vutbr.fit.ateam.web.dtos.checklist.ChecklistDTO;
import cz.vutbr.fit.ateam.web.dtos.label.LabelDTO;
import cz.vutbr.fit.ateam.web.dtos.task.TaskAttachmentDTO;
import cz.vutbr.fit.ateam.web.dtos.task.TaskCommentDTO;
import cz.vutbr.fit.ateam.web.dtos.task.TaskInfoDTO;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains method for managing tasks.
 */
public class TaskController {

    /**
     * Creates new task in database. If label is included, new labels are saved into DB also.
     * Only user who is assigned or author of the project can add task into that project.
     * Users which are not assigned or authors of projects will be not assigned to task.
     *
     * @param token         auth token
     * @param name          name of the task
     * @param state         string of task state
     * @param dueDateLong   task's due date
     * @param workedTime    total time user has worked on task
     * @param expectedHours expected hours
     * @param priority      string enum of priority
     * @param progress      from 0 to 100
     * @param usersId       assigned users
     * @param labels        assigned labels
     * @param parentTaskId  parent task's id
     * @param description   task's description
     * @param projectId     task's project id
     * @return Response
     */
    public static Response createTask(String token, String name, String state, Long dueDateLong, int workedTime, int expectedHours, String priority, short progress, int[] usersId, String[] labels, int parentTaskId, String description, int projectId) {

        Date dueDate = dueDateLong == null ? null : new Date(dueDateLong);
        /* starting transaction */
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) return Response.authError();

            /* creating new task */
            Task task = new Task();

            /* checking if project exists and if user has permission for that project */
            if (projectId != 0) {
                Project project = session.get(Project.class, projectId);
                if (project == null)
                    return Response.missingRecord("Project not found!");
                if (!project.userHasPermissionsToManage(user))
                    return Response.authError("User not permitted for project!");
                task.setProject(project);
            }

            /* assigning users to task */
            if (usersId != null) {
                task.setAssignedUsers(session, usersId);
            }

            /* assigning parent task */
            if (parentTaskId != 0) {
                Task parentTask = session.get(Task.class, parentTaskId);
                if (parentTask == null) return Response.missingRecord("Parent task not found!");
                if (parentTask.getProject().getId() != projectId)
                    return Response.actionDenied("Parent tak is not from the same project!");
                task.setParentTask(parentTask);
            }

            /* assigning labels */
            if (labels != null) {
                task.setLabels(session, labels);
            }

            /* assigning basic information */
            task.setOwner(user);
            task.setName(name);
            task.setState(Task.State.valueOf(state.toUpperCase()));
            task.setDueDate(dueDate);
            task.setWorkedTime(workedTime);
            task.setExpectedTime(expectedHours);
            task.setPriority(Task.Priority.valueOf(priority.toUpperCase()));
            task.setProgress(progress);
            task.setDescription(description);
            session.save(task);

            tx.commit();

            /*List<String> gcms = new ArrayList<>();
            gcms.add("fBILGr5sgjU:APA91bHCAJ5yfsJ5jrfyLohGhjbEtkLjgxiPXwr8V0BLYQwHbhsYuRtHfdLGusiaNUBfN1gW9Lpg6D_EdZD4WKnHtvpa7vQV6ItJ-5AwFdfFiTafihaAstgWZwjEzTsKAijUWCT002af");
            NotificationService.pushNotification(gcms, "Notifying" ,"PRDIS!");*/

            NotificationService.notifyCreated(user, task);

            return Response.ok(new TaskInfoDTO(task));

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Edit task.
     *
     * @return Response
     */
    public static Response editTask(Integer id, String token, String name, String state, Long dueDateLong, Integer workedTime, Integer expectedHours, String priority, Integer progress, int[] usersId, String[] labels, Integer parentTaskId, String description, Integer projectId) {

        Date dueDate = dueDateLong == null ? null : new Date(dueDateLong);
        /* starting transaction */
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) return Response.authError();

            /* getting task */
            Task task = session.get(Task.class, id);
            if (task == null) {
                return Response.missingRecord("Task not found!");
            }
            if (!task.userHasPermissionsToManage(user)) {
                return Response.authError("User unauthorized to manage task!");
            }

            /* checking if project exists and if user has permission for that project */
            if (projectId != null) {
                Project project = session.get(Project.class, projectId);
                if (project == null)
                    return Response.missingRecord("Project not found!");
                if (!project.userHasPermissionsToManage(user))
                    return Response.authError("User not permitted for project!");
                if (!task.canBeMovedIntoProject(project)) {
                    return Response.actionDenied("Not all users are in selected project!");
                }
                if (task.getParentTask() != null) {
                    return Response.actionDenied("Task can not be in the different project as parent task!");
                }
                task.setProject(project);
            }

            /* assigning users to task */
            if (usersId != null) {
                task.setAssignedUsers(session, usersId);
            }

            /* assigning parent task */
            if (parentTaskId != null) {
                Task parentTask = session.get(Task.class, parentTaskId);
                if (parentTask == null)
                    return Response.missingRecord("Parent task not found!");
                if (parentTask.getProject().getId() != task.getProject().getId())
                    return Response.actionDenied("Parent tak is not from the same project!");
                task.setParentTask(parentTask);
            }

            /* assigning labels */
            if (labels != null) {
                task.setLabels(session, labels);
            }

            /* assigning basic information */
            if (name != null) task.setName(name);
            if (state != null) task.setState(Task.State.valueOf(state.toUpperCase()));
            if (dueDate != null) task.setDueDate(dueDate);
            if (workedTime != null) task.setWorkedTime(workedTime);
            if (expectedHours != null) task.setExpectedTime(expectedHours);
            if (priority != null) task.setPriority(Task.Priority.valueOf(priority.toUpperCase()));
            if (progress != null) task.setProgress(progress.shortValue());
            if (description != null) task.setDescription(description);
            session.save(task);

            tx.commit();

            NotificationService.notifyUpdated(user, task);

            return Response.ok(new TaskInfoDTO(task));

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Delete tasks with all subtasks and comments and attachments etc.
     * Only user from the same project as task can delete task.
     *
     * @param token user's auth token
     * @param id    id of the task
     * @return Response
     */
    public static Response deleteTask(String token, Integer id) {
        /* starting transaction */
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            /* getting task */
            Task task = session.get(Task.class, id);
            if (task == null) {
                return Response.missingRecord("Task not found!");
            }
            if (!task.userHasPermissionsToManage(user)) {
                return Response.authError("User unauthorized to manage task!");
            }

            session.delete(task);

            tx.commit();

            NotificationService.notifyDeleted(user, task);

            return Response.ok("Task deleted!");

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Returns detailed infro about task.
     * Only user from task's project can view this info.
     *
     * @param token user's auth token
     * @param id    id f the task
     * @return Response
     */
    public static Response getTaskInfo(String token, Integer id) {
        try (Session session = HibernateUtil.getSession()) {

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            /* getting task */
            Task task = session.get(Task.class, id);
            if (task == null) {
                return Response.missingRecord("Task not found!");
            }
            if (!task.userHasPermissionsToManage(user)) {
                return Response.authError("User unauthorized to manage task!");
            }

            return Response.ok(new TaskInfoDTO(task));

        } catch (HibernateException e) {
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Assign user to task.
     *
     * @param token   user's auth token
     * @param taskId  id of the task
     * @param usersId ids of assigned users
     * @return Response
     */
    public static Response assignUsersToTask(String token, Integer taskId, List<Integer> usersId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            /* getting task */
            Task task = session.get(Task.class, taskId);
            if (task == null) {
                return Response.missingRecord("Task not found!");
            }
            if (!task.userHasPermissionsToManage(user)) {
                return Response.authError("User unauthorized to manage task!");
            }

            for (Integer userId : usersId) {
                User assignee = session.get(User.class, userId);
                if (assignee == null) {
                    return Response.missingRecord("User not found!");
                }
                if (!task.userHasPermissionsToManage(assignee)) {
                    return Response.authError("Assignee is not in task's project!");
                }

                task.getAssignedUsers().add(assignee);
            }

            session.save(task);

            tx.commit();

            NotificationService.notifyUpdated(user, task);

            return Response.ok("User assigned!");

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Removes assigned user from task.
     *
     * @param token  user's auth token
     * @param taskId id of the task
     * @param userId id of the user, who will be removed
     * @return Response
     */
    public static Response removeUserFromTask(String token, Integer taskId, Integer userId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            /* getting task */
            Task task = session.get(Task.class, taskId);
            if (task == null) {
                return Response.missingRecord("Task not found!");
            }
            if (!task.userHasPermissionsToManage(user)) {
                return Response.authError("User unauthorized to manage task!");
            }

            User userToRemove = session.get(User.class, userId);
            if (userToRemove == null) {
                return Response.missingRecord("User not found!");
            }
            if (!task.getAssignedUsers().contains(userToRemove)) {
                return Response.actionDenied("User not assigned to project");
            }

            task.getAssignedUsers().remove(userToRemove);
            session.save(task);

            tx.commit();

            NotificationService.notifyUpdated(user, task);

            return Response.ok("User removed!");

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Creates new comment and assign it to task.
     *
     * @param token       user's auth token
     * @param taskId      id of the task
     * @param description comment's text
     * @return Response
     */
    public static Response createComment(String token, Integer taskId, String description) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            /* getting task */
            Task task = session.get(Task.class, taskId);
            if (task == null) {
                return Response.missingRecord("Task not found!");
            }
            if (!task.userHasPermissionsToManage(user)) {
                return Response.authError("User unauthorized to manage task!");
            }

            Comment comment = new Comment();
            comment.setAuthor(user);
            comment.setDescription(description);
            comment.setTask(task);
            session.save(comment);

            tx.commit();

            NotificationService.notifyUpdated(user, task);

            return Response.ok(new TaskCommentDTO(comment));

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Edits description of the comment. Only author of the comment can manage it.
     *
     * @param token       user's auth token
     * @param id          comment's id
     * @param description new description
     * @return Response
     */
    public static Response editComment(String token, Integer id, String description) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Comment comment = session.get(Comment.class, id);
            if (comment == null) {
                return Response.missingRecord("Comment not found!");
            }
            if (!comment.getAuthor().equals(user)) {
                return Response.authError("User can not manage others comments!");
            }

            comment.setDescription(description);
            session.save(comment);

            tx.commit();

            NotificationService.notifyUpdated(user, comment);

            return Response.ok(new TaskCommentDTO(comment));

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Deletes comment. Only author of the comment can manage it.
     *
     * @param token user's auth token
     * @param id    comment's id
     * @return Response
     */
    public static Response deleteComment(String token, Integer id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Comment comment = session.get(Comment.class, id);
            if (comment == null) {
                return Response.missingRecord("Comment not found!");
            }
            if (!comment.getAuthor().equals(user)) {
                return Response.authError("User can not manage others comments!");
            }

            session.delete(comment);

            tx.commit();

            NotificationService.notifyDeleted(user, comment);

            return Response.ok("Comment deleted!");

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Add label to task. If label do not exists, creates new label.
     *
     * @param token  user's token
     * @param taskId task's id
     * @param name   label's name
     * @return Response
     */
    public static Response addLabel(String token, Integer taskId, String name) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            /* getting task */
            Task task = session.get(Task.class, taskId);
            if (task == null) {
                return Response.missingRecord("Task not found!");
            }
            if (!task.userHasPermissionsToManage(user)) {
                return Response.authError("User unauthorized to manage task!");
            }

            Label label = Label.getOrCreateNew(session, name);

            task.getLabels().add(label);
            session.save(task);

            tx.commit();

            NotificationService.notifyUpdated(user, task);

            return Response.ok(new LabelDTO(label));

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Deletes label from task. If label is no longer assigned to any task,
     * it is removed.
     *
     * @param token   user's auth token
     * @param taskId  task's id
     * @param labelId label's id
     * @return Response
     */
    public static Response deleteLabel(String token, Integer taskId, Integer labelId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            /* getting task */
            Task task = session.get(Task.class, taskId);
            if (task == null) {
                return Response.missingRecord("Task not found!");
            }
            if (!task.userHasPermissionsToManage(user)) {
                return Response.authError("User unauthorized to manage task!");
            }

            Label label = session.get(Label.class, labelId);
            if (label == null) {
                return Response.missingRecord("Label not found!");
            }

            if (!task.getLabels().contains(label)) {
                return Response.missingRecord("Label not found in task!");
            }

            task.getLabels().remove(label);
            session.save(task);

            label.getTasks().remove(task);
            if (label.getTasks().size() == 0) {
                session.delete(label);
            }

            tx.commit();

            NotificationService.notifyUpdated(user, task);

            return Response.ok("Label removed!");

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Adds checklist to task.
     *
     * @param token user's auth token
     * @param taskId task's id
     * @param name name of the checklist
     * @param items checklist items
     * @return Response
     */
    public static Response addChecklist(String token, Integer taskId, String name, List<String> items) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            /* getting task */
            Task task = session.get(Task.class, taskId);
            if (task == null) {
                return Response.missingRecord("Task not found!");
            }
            if (!task.userHasPermissionsToManage(user)) {
                return Response.authError("User unauthorized to manage task!");
            }

            Checklist checklist = new Checklist();
            checklist.setName(name);
            checklist.setTask(task);
            checklist.setChecklistItems(
                    items.stream()
                            .map(description -> {
                                ChecklistItem item = new ChecklistItem();
                                item.setCreatedAt();
                                item.setDone(false);
                                item.setDescription(description);
                                item.setChecklist(checklist);
                                return item;
                            })
                            .collect(Collectors.toSet()));
            session.save(checklist);
            tx.commit();

            NotificationService.notifyUpdated(user, task);

            return Response.ok(new ChecklistDTO(checklist));

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        }
    }

    /**
     * Saves attachment into server.
     *
     * @param token       user's auth token
     * @param taskId      task's id
     * @param description file description
     * @param file        file
     * @param name        file's name
     * @return Response
     */
    public static Response addAttachment(String token, Integer taskId, String description, MultipartFile file, String name) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            /* getting task */
            Task task = session.get(Task.class, taskId);
            if (task == null) {
                return Response.missingRecord("Task not found!");
            }
            if (!task.userHasPermissionsToManage(user)) {
                return Response.authError("User unauthorized to manage task!");
            }

            if (file.isEmpty()) {
                return Response.paramMissing("File not uploaded!");
            }

            if (file.getSize() > Attachment.maxAttachemntSize()) {
                return Response.paramMissing("File too large!");
            }

            Attachment attachment = new Attachment();
            attachment.setTask(task);
            attachment.setAuthor(user);
            attachment.setDescription(description);
            attachment.setMimeType(file.getContentType());
            attachment.setName(name);
            session.save(attachment);

            String realFileName = attachment.getId() + "_" + name;

            File outputFile = new File(Attachment.getPath() + "/" + realFileName);
            if (!outputFile.getParentFile().isDirectory()) {
                if (!outputFile.getParentFile().mkdirs()) {
                    throw new Exception("Cannot make directory for file!");
                }
            }
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(outputFile));
            FileCopyUtils.copy(file.getInputStream(), stream);
            stream.close();

            attachment.setFilePath(Attachment.getPath() + "/" + realFileName);
            session.save(attachment);

            tx.commit();

            NotificationService.notifyUpdated(user, task);

            return Response.ok(new TaskAttachmentDTO(attachment));

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.operationFailed(e.getMessage());
        }
    }

    /**
     * Deletes attachment, also with file saved on disk.
     *
     * @param token user's auth token
     * @param id    attachment's id
     * @return Response
     */
    public static Response deleteAttachment(String token, Integer id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Attachment attachment = session.get(Attachment.class, id);
            if (attachment == null) {
                return Response.missingRecord("Attachment not found!");
            }

            session.delete(attachment);
            tx.commit();

            NotificationService.notifyDeleted(user, attachment);

            return Response.ok("Attachment deleted!");

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.operationFailed(e.getMessage());
        }
    }

    /**
     * Returns file. No JSON is returned only raw data.
     *
     * @param token    user's auth token
     * @param id       attachment's id
     * @param response file's data
     */
    public static void getAttachmentFile(String token, Integer id, HttpServletResponse response) {

        try (Session session = HibernateUtil.getSession()) {

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                response.setStatus(401);
                return;
            }

            Attachment attachment = session.get(Attachment.class, id);
            if (attachment == null) {
                response.setStatus(404);
                return;
            }

            File inputFile = new File(attachment.getFilePath());
            InputStream stream = new FileInputStream(inputFile);
            IOUtils.copy(stream, response.getOutputStream());
            response.setContentType(attachment.getMimeType());
            response.setHeader("Content-Disposition", "attachment; filename=" + attachment.getName());
            response.flushBuffer();

            stream.close();
        } catch (Exception ignored) {
        }
    }

    /**
     * Edits task's attachment.
     *
     * @param token user's auth token
     * @param id attachment's id
     * @param description new description
     * @return Response
     */
    public static Response editAttachment(String token, Integer id, String description) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            /* authenticate user */
            User user = Auth.getUserByAuthToken(session, token);
            if (user == null) {
                return Response.authError();
            }

            Attachment attachment = session.get(Attachment.class, id);
            if (attachment == null) {
                return Response.missingRecord("Attachment not found!");
            }

            attachment.setDescription(description);
            session.save(attachment);

            tx.commit();

            NotificationService.notifyUpdated(user, attachment);

            return Response.ok(new TaskAttachmentDTO(attachment));

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.hibernateException(e.getMessage(), null);
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            return Response.operationFailed(e.getMessage());
        }
    }
}

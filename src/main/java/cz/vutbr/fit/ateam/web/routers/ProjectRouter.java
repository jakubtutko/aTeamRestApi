package cz.vutbr.fit.ateam.web.routers;

import cz.vutbr.fit.ateam.web.commons.Response;
import cz.vutbr.fit.ateam.web.controllers.ProjectController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectRouter {

    /**
     * Delete user from project.
     */
    @RequestMapping(value = "/{projectId}/users", method = RequestMethod.DELETE)
    public Response removeUser(
            @PathVariable(value = "projectId") int projectId,
            @RequestHeader(name = "token") String token,
            @RequestParam(value = "userEmail") String userEmail
    ) {
        return ProjectController.deleteUser(token, projectId, userEmail);
    }

    /**
     * Get users assigned to project.
     */
    @RequestMapping(value = "/{projectId}/users", method = RequestMethod.GET)
    public Response getUsers(
            @PathVariable(value = "projectId") int projectId,
            @RequestHeader(name = "token") String token
    ) {
        return ProjectController.getUsers(token, projectId);
    }

    /**
     * Add users to project.
     */
    @RequestMapping(value = "/{projectId}/users", method = RequestMethod.POST)
    public Response addUsers(
            @PathVariable(value = "projectId") int projectId,
            @RequestHeader(name = "token") String token,
            @RequestParam(name = "userEmail") List<String> usersEmails
    ) {
        return ProjectController.addUsers(projectId, token, usersEmails);
    }

    /**
     * Update project.
     */
    @RequestMapping(value = "/{projectId}", method = RequestMethod.PUT)
    public Response update(
            @PathVariable(value = "projectId") int projectId,
            @RequestHeader(name = "token") String token,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "color", required = false) Integer color
    ) {
        return ProjectController.update(token, projectId, name, description, color);
    }

    /**
     * Delete project.
     */
    @RequestMapping(value = "/{projectId}", method = RequestMethod.DELETE)
    public Response delete(
            @PathVariable(value = "projectId") int projectId,
            @RequestHeader(name = "token") String token
    ) {
        return ProjectController.delete(token, projectId);
    }

    /**
     * Create new project.
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Response createNew(
            @RequestHeader(name = "token") String token,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "color", required = false) Integer color
    ) {
        return ProjectController.createProject(token, name, description, color);
    }

    /**
     * Get project of given ID..
     */
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public Response getProject(
            @RequestHeader(name = "token") String token,
            @PathVariable(value = "projectId") int projectId
    ) {
        return ProjectController.getProject(token, projectId);
    }

    /**
     * Get all user's projects.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Response getAll(
            @RequestHeader(name = "token") String token
    ) {
        return ProjectController.getAll(token);
    }

    /**
     * Get all user's projects.
     */
    @RequestMapping(value = "/created", method = RequestMethod.GET)
    public Response getCreated(
            @RequestHeader(name = "token") String token
    ) {
        return ProjectController.getCreatedProjects(token);
    }

    /**
     * Get all user's projects.
     */
    @RequestMapping(value = "/participated", method = RequestMethod.GET)
    public Response getParticipated(
            @RequestHeader(name = "token") String token
    ) {
        return ProjectController.getParticipatedProjects(token);
    }

    /**
     * Get project's tasks.
     */
    @RequestMapping(value = "/{projectId}/tasks", method = RequestMethod.GET)
    public Response getProjectTasks(
            @RequestHeader(name = "token") String token,
            @PathVariable(value = "projectId") int projectId,
            @RequestParam(value = "until", required = false) Long until
    ) {
        return ProjectController.getProjectTasks(token, projectId, until);
    }
}

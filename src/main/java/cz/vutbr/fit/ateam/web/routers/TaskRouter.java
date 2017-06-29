package cz.vutbr.fit.ateam.web.routers;

import cz.vutbr.fit.ateam.web.commons.Response;
import cz.vutbr.fit.ateam.web.controllers.TaskController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Routes request to correct controller methods.
 */
@RestController
@RequestMapping("/")
public class TaskRouter {
    @RequestMapping(value = "/tasks/", method = RequestMethod.POST)
    public Response createTask(
            @RequestHeader(name = "token") String token,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "state") String state,
            @RequestParam(value = "dueDate", required = false) Long dueDate,
            @RequestParam(value = "workedTime", required = false, defaultValue = "0") int workedTime,
            @RequestParam(value = "expectedHours", required = false, defaultValue = "0") int expectedHours,
            @RequestParam(value = "priority") String priority,
            @RequestParam(value = "progress", required = false, defaultValue = "0") short progress,
            @RequestParam(value = "usersId", required = false) int[] usersId,
            @RequestParam(value = "labels", required = false) String[] labels,
            @RequestParam(value = "parentTaskId", required = false, defaultValue = "0") int parentTaskId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId
    ) {
        return TaskController.createTask(token, name, state, dueDate, workedTime, expectedHours, priority, progress, usersId, labels, parentTaskId, description, projectId);
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.PUT)
    public Response editTask(
            @PathVariable(value = "id") Integer id,
            @RequestHeader(name = "token") String token,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "dueDate", required = false) Long dueDate,
            @RequestParam(value = "workedTime", required = false) Integer workedTime,
            @RequestParam(value = "expectedHours", required = false) Integer expectedHours,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "progress", required = false) Integer progress,
            @RequestParam(value = "usersId", required = false) int[] usersId,
            @RequestParam(value = "labels", required = false) String[] labels,
            @RequestParam(value = "parentTaskId", required = false) Integer parentTaskId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "projectId", required = false) Integer projectId
    ) {
        return TaskController.editTask(id, token, name, state, dueDate, workedTime, expectedHours, priority, progress, usersId, labels, parentTaskId, description, projectId);
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.DELETE)
    public Response deleteTask(
            @PathVariable(value = "id") Integer id,
            @RequestHeader(value = "token") String token
    ) {
        return TaskController.deleteTask(token, id);
    }

    @RequestMapping(value = "/tasks/{id}", method = RequestMethod.GET)
    public Response getTaskInfo(
            @PathVariable(value = "id") Integer id,
            @RequestHeader(value = "token") String token
    ) {
        return TaskController.getTaskInfo(token, id);
    }

    @RequestMapping(value = "/tasks/{taskId}/users", method = RequestMethod.POST)
    public Response assignUserToTask(
            @PathVariable("taskId") Integer taskId,
            @RequestParam(name = "usersId") List<Integer> usersId,
            @RequestHeader(value = "token") String token
    ) {
        return TaskController.assignUsersToTask(token, taskId, usersId);
    }

    @RequestMapping(value = "/tasks/{taskId}/users/{userId}", method = RequestMethod.DELETE)
    public Response removeUserFromTask(
            @PathVariable("taskId") Integer taskId,
            @PathVariable("userId") Integer userId,
            @RequestHeader(value = "token") String token
    ) {
        return TaskController.removeUserFromTask(token, taskId, userId);
    }

    @RequestMapping(value = "/tasks/{taskId}/comments", method = RequestMethod.POST)
    public Response createComment(
            @PathVariable("taskId") Integer taskId,
            @RequestHeader(value = "token") String token,
            @RequestParam(value = "description") String description
    ) {
        return TaskController.createComment(token, taskId, description);
    }

    @RequestMapping(value = "/comments/{id}", method = RequestMethod.PUT)
    public Response editComment(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "token") String token,
            @RequestParam(value = "description") String description
    ) {
        return TaskController.editComment(token, id, description);
    }

    @RequestMapping(value = "/comments/{id}", method = RequestMethod.DELETE)
    public Response deleteComment(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "token") String token
    ) {
        return TaskController.deleteComment(token, id);
    }

    @RequestMapping(value = "/tasks/{taskId}/labels", method = RequestMethod.POST)
    public Response addLabel(
            @PathVariable("taskId") Integer taskId,
            @RequestHeader(value = "token") String token,
            @RequestParam(value = "name") String name
    ) {
        return TaskController.addLabel(token, taskId, name);
    }

    @RequestMapping(value = "/tasks/{taskId}/labels/{labelId}", method = RequestMethod.DELETE)
    public Response deleteLabel(
            @PathVariable("taskId") Integer taskId,
            @RequestHeader(value = "token") String token,
            @PathVariable("labelId") Integer labelId) {
        return TaskController.deleteLabel(token, taskId, labelId);
    }

    @RequestMapping(value = "/tasks/{taskId}/checklists", method = RequestMethod.POST)
    public Response addChecklist(
            @PathVariable("taskId") Integer taskId,
            @RequestHeader(value = "token") String token,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "itemDescription", required = false) List<String> items
    ) {
        return TaskController.addChecklist(token, taskId, name, items);
    }

    @RequestMapping(value = "/tasks/{taskId}/attachments", method = RequestMethod.POST)
    public Response addAttachment(
            @PathVariable("taskId") Integer taskId,
            @RequestHeader(value = "token") String token,
            @RequestHeader(value = "description", required = false) String description,
            @RequestBody() MultipartFile file,
            @RequestHeader(value = "name") String name
    ) {
        return TaskController.addAttachment(token, taskId, description, file, name);
    }

    @RequestMapping(value = "/attachments/{id}", method = RequestMethod.PUT)
    public Response editAttachment(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "token") String token,
            @RequestParam(value = "description") String description
    ) {
        return TaskController.editAttachment(token, id, description);
    }

    @RequestMapping(value = "/attachments/{id}", method = RequestMethod.DELETE)
    public Response deleteAttachment(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "token") String token
    ) {
        return TaskController.deleteAttachment(token, id);
    }

    @RequestMapping(value = "/attachments/{id}/file", method = RequestMethod.GET)
    public void getAttachmentFile(
            @PathVariable("id") Integer id,
            @RequestHeader(value = "token") String token,
            HttpServletResponse response
    ) {
        TaskController.getAttachmentFile(token, id, response);
    }
}

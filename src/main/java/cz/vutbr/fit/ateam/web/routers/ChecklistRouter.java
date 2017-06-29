package cz.vutbr.fit.ateam.web.routers;

import cz.vutbr.fit.ateam.web.commons.Response;
import cz.vutbr.fit.ateam.web.controllers.ChecklistController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class ChecklistRouter {

    @RequestMapping(value = "/checklists/{id}/items", method = RequestMethod.POST)
    public Response addToChecklist(
            @PathVariable(value = "id") int id,
            @RequestHeader(value = "token") String token,
            @RequestParam(value = "description") List<String> descriptions
    ) {
        return ChecklistController.addItemToChecklist(token, id, descriptions);
    }

    @RequestMapping(value = "/checklists/{id}", method = RequestMethod.DELETE)
    public Response deleteChecklist(
            @PathVariable(value = "id") int id,
            @RequestHeader(value = "token") String token
    ) {
        return ChecklistController.deleteChecklist(token, id);
    }

    @RequestMapping(value = "/checklists/items/{id}", method = RequestMethod.PUT)
    public Response editChecklistItem(
            @PathVariable(value = "id") int id,
            @RequestHeader(value = "token") String token,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isDone", required = false) Boolean isDone
    ) {
        return ChecklistController.editChecklistItem(id, token, description, isDone);
    }

    @RequestMapping(value = "/checklists/items/{id}", method = RequestMethod.DELETE)
    public Response deleteChecklistItem(
            @PathVariable(value = "id") int id,
            @RequestHeader(value = "token") String token
    ) {
        return ChecklistController.deleteChecklistItem(id, token);
    }
}

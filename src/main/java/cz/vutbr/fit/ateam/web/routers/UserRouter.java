package cz.vutbr.fit.ateam.web.routers;

import cz.vutbr.fit.ateam.web.commons.Response;
import cz.vutbr.fit.ateam.web.controllers.UserController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserRouter {

    @RequestMapping(value = "/users/", method = RequestMethod.POST)
    public Response registerUser(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "gcm", required = false) String gcm,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName

    ) {
        return UserController.register(email, password, gcm, firstName, lastName);
    }

    @RequestMapping(value = "/login/", method = RequestMethod.POST)
    public Response loginUser(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "gcm", required = false) String gcm

    ) {
        return UserController.login(email, password, gcm);
    }

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public Response updateUser(
            @RequestHeader(value = "token") String token,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "gcm", required = false) String gcm,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName
    ) {
        return UserController.update(token, email, password, gcm, firstName, lastName);
    }

    /**
     * Get user's task.
     */
    @RequestMapping(value = "/users/tasks", method = RequestMethod.GET)
    public Response getProjectTasks(
            @RequestHeader(name = "token") String token,
            @RequestParam(value = "until", required = false) Long until
    ) {
        return UserController.geTasks(token, until);
    }

    /**
     * Get user's task.
     */
    @RequestMapping(value = "/users/ping", method = RequestMethod.GET)
    public Response getProjectTasks(
            @RequestHeader(name = "token") String token
    ) {
        return UserController.ping(token);
    }

    /**
     * Set user's gcm.
     */
    @RequestMapping(value = "/users/gcm", method = RequestMethod.POST)
    public Response setUserGcm(
            @RequestHeader(name = "token") String token,
            @RequestParam(name = "gcm") String gcm
    ) {
        return UserController.setUserGcm(token, gcm);
    }
}

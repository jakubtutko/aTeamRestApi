package cz.vutbr.fit.ateam.web.commons;

import com.google.android.gcm.server.InvalidRequestException;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import cz.vutbr.fit.ateam.persistence.models.BaseModel;
import cz.vutbr.fit.ateam.persistence.models.User;
import cz.vutbr.fit.ateam.web.dtos.notification.NotificationDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Sends google notification.
 */
@Service
public class NotificationService {

    /**
     * Sends created type notification.
     *
     * @param user user who created model
     * @param model new model
     */
    public static void notifyCreated(User user, BaseModel model) {
        if (user == null || model == null) {
            return;
        }
        NotificationDTO notification = new NotificationDTO(NotificationDTO.Events.CREATED, user, model);
        NotificationService.pushNotification(model.acquireUsersGcms(), notification);
    }

    /**
     * Sends updated type notification.
     *
     * @param user user who deleted model
     * @param model deleted model
     */
    public static void notifyUpdated(User user, BaseModel model) {
        if (user == null || model == null) {
            return;
        }
        NotificationDTO notification = new NotificationDTO(NotificationDTO.Events.UPDATED, user, model);
        NotificationService.pushNotification(model.acquireUsersGcms(), notification);
    }

    /**
     * Sends deleted type notification.
     *
     * @param user user who updated model
     * @param model updated model
     */
    public static void notifyDeleted(User user, BaseModel model) {
        if (user == null || model == null) {
            return;
        }
        NotificationDTO notification = new NotificationDTO(NotificationDTO.Events.DELETED, user, model);
        NotificationService.pushNotification(model.acquireUsersGcms(), notification);
    }

    /**
     * Sends notification to given gcms.
     *
     * @param gcmIds list of all gcms
     * @param body body of the notification
     */
    static void pushNotification(List<String> gcmIds, NotificationDTO body) {
        if (gcmIds == null || gcmIds.size() == 0) {
            return;
        }

        Gson gson = new Gson();
        final String GCM_API_KEY = "AIzaSyAhSYEYxEa-hOdpBSDVapJW_7qmGKzTqF4";
        final int retries = 3;
        Sender sender = new Sender(GCM_API_KEY);
        Message msg = new Message.Builder().addData("ATeamRestApi server's message", gson.toJson(body)).build();
        // System.out.println("INFO: " + gson.toJson(body));
        try {
            MulticastResult results = sender.send(msg, gcmIds, retries);

            results.getResults().stream().filter(result
                    -> result.getErrorCodeName() != null).forEach(result
                    -> System.err.println("ERROR: Notification not sent! " + result.getErrorCodeName()));

        } catch (InvalidRequestException e) {
            System.err.println("ERROR: Invalid Request! " + e.getMessage());
        } catch (IOException e) {
            System.err.println("ERROR: IO Exception! " + e.getMessage());
        }
    }
}

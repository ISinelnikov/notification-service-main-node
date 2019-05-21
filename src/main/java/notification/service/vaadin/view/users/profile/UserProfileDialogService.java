package notification.service.vaadin.view.users.profile;

import notification.service.backend.domain.UserProfile;
import notification.service.backend.service.event.EventService;
import notification.service.backend.service.notification.SendingHistoryService;

import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class UserProfileDialogService {
    private final SendingHistoryService sendingHistoryService;
    private final EventService eventService;

    public UserProfileDialogService(SendingHistoryService sendingHistoryService,
            EventService eventService) {
        this.sendingHistoryService = Objects.requireNonNull(sendingHistoryService,
                "History service can't be null.");
        this.eventService = Objects.requireNonNull(eventService,
                "Event service can't be null.");
    }

    public void openUserProfileDialog(UserProfile profile) {
        Objects.requireNonNull(profile, "Profile can't be null.");
        new UserProfileWindow(profile, eventService.getUserHistoryById(profile.getUserId()),
                sendingHistoryService.getUserHistoryById(profile.getUserId()));
    }
}

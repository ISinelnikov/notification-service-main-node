package notification.service.vaadin.view.users.profile;

import notification.service.backend.domain.UserProfile;
import notification.service.backend.service.notification.SendingHistoryService;

import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class UserProfileDialogService {
    private final SendingHistoryService sendingHistoryService;

    public UserProfileDialogService(SendingHistoryService sendingHistoryService) {
        this.sendingHistoryService = Objects.requireNonNull(sendingHistoryService,
                "History service can't be null.");
    }

    public void openUserProfileDialog(UserProfile profile) {
        Objects.requireNonNull(profile, "Profile can't be null.");
        new UserProfileWindow(profile, sendingHistoryService.getUserHistoryById(profile.getUserId()));
    }
}

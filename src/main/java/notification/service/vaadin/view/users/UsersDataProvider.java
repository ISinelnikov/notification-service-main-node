package notification.service.vaadin.view.users;

import notification.service.backend.domain.UserProfile;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.backend.service.UserService;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vaadin.flow.data.provider.ListDataProvider;

@Service
public class UsersDataProvider extends ListDataProvider<UserProfile> {
    private static final long serialVersionUID = -4314671148795922590L;

    private final UserService userService;

    @Autowired
    public UsersDataProvider(UserService userService) {
        super(Objects.requireNonNull(userService.getAllUserProfiles(),
                "UserProfile service can't be null."));
        this.userService = userService;
    }

    public void removeUser(UserProfile userProfile) throws ModelModificationException {
        userService.removeUser(userProfile);
    }
}

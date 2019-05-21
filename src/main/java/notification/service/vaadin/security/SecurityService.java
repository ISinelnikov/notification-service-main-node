package notification.service.vaadin.security;

import java.io.Serializable;

public interface SecurityService extends Serializable {
    boolean signIn(String username, String password);

    boolean isUserSignedIn();
}

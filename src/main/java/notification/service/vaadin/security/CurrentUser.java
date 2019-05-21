package notification.service.vaadin.security;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

public final class CurrentUser {

    public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = "SESSION_ID";

    private CurrentUser() {
    }

    public static String get() {
        return (String) getCurrentRequest().getWrappedSession()
                .getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
    }

    public static void set(String currentUser) {
        if (currentUser == null) {
            getCurrentRequest().getWrappedSession().removeAttribute(
                    CURRENT_USER_SESSION_ATTRIBUTE_KEY);
        } else {
            getCurrentRequest().getWrappedSession().setAttribute(
                    CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);
        }
    }

    private static VaadinRequest getCurrentRequest() {
        VaadinRequest request = VaadinService.getCurrentRequest();
        if (request == null) {
            throw new IllegalStateException("No request bound to current thread.");
        }
        return request;
    }
}

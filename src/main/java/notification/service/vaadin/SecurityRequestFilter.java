package notification.service.vaadin;

import notification.service.vaadin.security.SecurityService;
import notification.service.vaadin.view.login.LoginView;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

@Component
public class SecurityRequestFilter implements VaadinServiceInitListener {
    private final SecurityService securityService;

    @Autowired
    public SecurityRequestFilter(SecurityService securityService) {
        this.securityService = Objects.requireNonNull(securityService, "Security service can't be null.");
    }

    @Override
    public void serviceInit(ServiceInitEvent initEvent) {
        initEvent.getSource().addUIInitListener(uiInitEvent -> {
            uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
                if (!securityService.isUserSignedIn() && !LoginView.class
                        .equals(enterEvent.getNavigationTarget()))
                    enterEvent.rerouteTo(LoginView.class);
            });
        });
    }
}

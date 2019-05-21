package notification.service.vaadin.view.login;

import notification.service.vaadin.security.SecurityService;
import notification.service.vaadin.view.notification.NotificationView;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Login")
public class LoginView extends FlexLayout {
    private static final long serialVersionUID = 7905926317302102615L;

    private final SecurityService securityService;

    public LoginView(@Autowired SecurityService securityService) {
        this.securityService = Objects.requireNonNull(securityService,
                "Security service can't be null.");

        LoginForm loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);

        FlexLayout centeringLayout = new FlexLayout();
        centeringLayout.setSizeFull();
        centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        centeringLayout.setAlignItems(Alignment.CENTER);

        loginForm.addLoginListener(event -> {
            loginForm.setEnabled(false);
            try {
                login(event.getUsername(), event.getPassword());
            } finally {
                loginForm.setEnabled(true);
            }
        });
        centeringLayout.add(loginForm);
        add(centeringLayout);
    }

    private void login(String username, String password) {
        if (securityService.signIn(username.trim(), password)) {
            getUI().ifPresent(ui -> ui.navigate(NotificationView.VIEW_ROUTE));
        } else {
            Notification.show("Login failed. Please check your username and password and try again.")
                    .setPosition(Notification.Position.MIDDLE);
        }
    }
}

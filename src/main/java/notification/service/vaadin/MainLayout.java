package notification.service.vaadin;

import notification.service.vaadin.view.notification.NotificationView;
import notification.service.vaadin.view.security.SecurityView;
import notification.service.vaadin.view.users.UserView;

import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@HtmlImport("frontend://css/shared-styles.html")
@Theme(value = Lumo.class)
public class MainLayout extends FlexLayout implements RouterLayout {
    private static final long serialVersionUID = 5563582221215161855L;

    public MainLayout() {
        setSizeFull();
        setClassName("main-layout");

        Menu menu = new Menu();

        // Notification template
        menu.addView(NotificationView.class, NotificationView.VIEW_NAME,
                VaadinIcon.NOTEBOOK.create());

        // User panel
        menu.addView(UserView.class, UserView.VIEW_NAME,
                VaadinIcon.USER_CARD.create());

        // Security view
        menu.addView(SecurityView.class, SecurityView.VIEW_NAME,
                VaadinIcon.LOCK.create());

        add(menu);
    }
}

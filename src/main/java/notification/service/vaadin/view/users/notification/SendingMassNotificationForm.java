package notification.service.vaadin.view.users.notification;

import notification.service.backend.domain.FirebaseCredentials;
import notification.service.backend.domain.UserProfile;
import notification.service.backend.service.FirebaseCredentialsService;
import notification.service.backend.service.NotificationTemplateService;
import notification.service.backend.service.UserService;
import notification.service.backend.service.notification.NotificationSendingService;
import notification.service.domain.notification.firebase.NotificationTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Component
public class SendingMassNotificationForm {
    private final NotificationTemplateService templateService;
    private final NotificationSendingService notificationSendingService;
    private final UserService userService;

    public SendingMassNotificationForm(NotificationTemplateService templateService,
            NotificationSendingService notificationSendingService,
            UserService userService) {
        this.templateService = Objects.requireNonNull(templateService, "Template service can't be null.");
        this.notificationSendingService = Objects.requireNonNull(notificationSendingService,
                "Notification sending service can't be null.");
        this.userService = userService;
    }

    public void open() {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        ComboBox<Notification> templates = new ComboBox<>("Template");
        templates.setItems(templateService.getAllNotificationTemplate());
        templates.setItemLabelGenerator(NotificationTemplate::getTemplateTitle);
        templates.setRequired(true);
        templates.setSizeFull();

        ComboBox<FirebaseCredentials> application = new ComboBox<>("Application");
        application.setItems(firebaseCredentialsService.getAllFirebaseCredentials());
        application.setItemLabelGenerator(FirebaseCredentials::getApplicationName);
        application.setRequired(true);
        application.setSizeFull();

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSizeFull();

        Button cancel = new Button("Cancel", event -> dialog.close());
        cancel.setSizeFull();
        Button send = new Button("Send", event -> {
            NotificationTemplate value = templates.getValue();
            FirebaseCredentials selectedApplication = application.getValue();
            if (value != null && selectedApplication != null) {

                //Filter user profiles by package name
                List<String> userIds = userService.getAllUserProfiles()
                        .stream()
                        .filter(profile -> profile.getPackageName().equals(selectedApplication.getPackageName()))
                        .map(UserProfile::getUserId)
                        .collect(Collectors.toList());

                Map<String, Optional<String>> idToStatus = notificationSendingService
                        .sendMassNotification(value, userIds);
                idToStatus.forEach((k, v) -> Notification.show(k + ": " + v).setPosition(Notification.Position.MIDDLE));

                Notification.show("All notification was sending.");
            } else {
                Notification.show("'Template' can't be empty.");
            }
        });
        send.setSizeFull();
        buttonLayout.add(cancel, send);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(templates, application, buttonLayout);
        dialog.add(mainLayout);
        dialog.open();
    }
}

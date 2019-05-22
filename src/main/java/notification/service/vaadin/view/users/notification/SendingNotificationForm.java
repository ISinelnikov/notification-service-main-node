package notification.service.vaadin.view.users.notification;

import notification.service.backend.domain.UserProfile;
import notification.service.backend.service.NotificationTemplateService;
import notification.service.backend.service.notification.NotificationSendingException;
import notification.service.backend.service.notification.NotificationSendingService;
import notification.service.domain.notification.rich.RichTemplate;

import java.util.Objects;
import org.springframework.stereotype.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

@Component
public class SendingNotificationForm {
    private final NotificationTemplateService templateService;
    private final NotificationSendingService notificationSendingService;

    public SendingNotificationForm(NotificationTemplateService templateService,
            NotificationSendingService notificationSendingService) {
        this.templateService = Objects.requireNonNull(templateService, "Template service can't be null.");
        this.notificationSendingService = Objects.requireNonNull(notificationSendingService,
                "Notification sending service can't be null.");
    }

    public void open(UserProfile userProfile) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        TextField userId = new TextField("User id");
        userId.setValue(userProfile.getUserId());
        userId.setReadOnly(true);
        userId.setSizeFull();

        TextField notificationId = new TextField("Email address: ");
        notificationId.setValue(userProfile.getEmailAddress());
        notificationId.setReadOnly(true);
        notificationId.setSizeFull();

        ComboBox<RichTemplate> templates = new ComboBox<>("Template");
        templates.setItems(templateService.getAllNotificationTemplate());
        templates.setItemLabelGenerator(RichTemplate::getTemplateTitle);
        templates.setRequired(true);
        templates.setSizeFull();

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSizeFull();

        Button cancel = new Button("Cancel", event -> dialog.close());
        cancel.setSizeFull();
        Button send = new Button("Send", event -> {
            RichTemplate value = templates.getValue();
            if (value != null) {
                try {
                    String status = notificationSendingService.sendNotification(value, userProfile.getUserId());
                    Notification.show(status).setPosition(Notification.Position.MIDDLE);
                } catch (NotificationSendingException e) {
                    Notification.show(e.getMessage()).setPosition(Notification.Position.MIDDLE);
                }
            } else {
                Notification.show("'Template' can't be empty.");
            }
        });
        send.setSizeFull();
        buttonLayout.add(cancel, send);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(userId, notificationId, templates, buttonLayout);
        dialog.add(mainLayout);
        dialog.open();
    }
}

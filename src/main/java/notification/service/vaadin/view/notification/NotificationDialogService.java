package notification.service.vaadin.view.notification;

import notification.service.domain.notification.firebase.NotificationTemplate;
import notification.service.vaadin.common.CrudService;
import notification.service.vaadin.view.notification.window.NotificationWindow;

import java.io.Serializable;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Service for open template window (create/show/edit)
 */
@Component
public class NotificationDialogService implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(NotificationDialogService.class);

    private final CrudService<NotificationTemplate> templateCrudService;

    public NotificationDialogService(CrudService<NotificationTemplate> templateCrudService) {
        this.templateCrudService = Objects.requireNonNull(templateCrudService,
                "Template crud service can't be null.");
    }

    public void openExistNotificationTemplate(NotificationTemplate template) {
        logger.debug("Open notification template with editable: {}.", template);
        new NotificationWindow(templateCrudService, template, true);
    }

    public void openEmptyNotificationTemplate() {
        logger.debug("Open notification template create form.");
        new NotificationWindow(templateCrudService);
    }

    public void openExistNotificationTemplateReadOnly(NotificationTemplate template) {
        logger.debug("Open notification template with editable: {}.", template);
        new NotificationWindow(templateCrudService, template, false);
    }
}

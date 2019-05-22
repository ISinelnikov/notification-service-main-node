package notification.service.backend.service;

import notification.service.backend.cache.NotificationTemplateCache;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.domain.notification.rich.RichTemplate;

import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateService.class);

    private final NotificationTemplateCache templateCache;

    public NotificationTemplateService(NotificationTemplateCache templateCache) {
        this.templateCache = Objects.requireNonNull(templateCache, "Template cache can't be null.");
    }

    public List<RichTemplate> getAllNotificationTemplate() {
        return templateCache.getAllFirebaseCredentials();
    }

    public void addNotificationTemplate(RichTemplate template) throws ModelModificationException {
        logger.debug("Create new notification template: {}.", template);
        templateCache.addNotificationTemplate(template);
    }

    public void updateNotificationTemplate(RichTemplate template) throws ModelModificationException {
        logger.debug("Update notification template: {}.", template);
        templateCache.updateNotificationTemplate(template);
    }

    public void removeNotificationTemplate(RichTemplate template) throws ModelModificationException {
        logger.debug("Remove notification template: {}.", template);
        templateCache.removeNotificationTemplate(template);
    }
}

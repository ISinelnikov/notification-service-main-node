package notification.service.vaadin.view.notification;

import notification.service.backend.repository.base.ModelModificationException;
import notification.service.backend.service.NotificationTemplateService;
import notification.service.domain.notification.firebase.NotificationTemplate;
import notification.service.vaadin.common.CrudService;

import java.util.Objects;
import org.springframework.stereotype.Service;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

@Service
public class NotificationCrudService implements CrudService<NotificationTemplate> {
    private final NotificationTemplateService templateService;
    private final ListDataProvider<NotificationTemplate> dataProvider;

    public NotificationCrudService(NotificationTemplateService templateService) {
        this.templateService = Objects.requireNonNull(templateService, "Template service can't be null.");
        this.dataProvider = DataProvider.ofCollection(templateService
                .getAllNotificationTemplate());
    }

    public ListDataProvider<NotificationTemplate> getDataProvider() {
        return dataProvider;
    }

    @Override
    public void saveModel(NotificationTemplate template) throws ModelModificationException {
        templateService.addNotificationTemplate(template);
    }

    @Override
    public void updateModel(NotificationTemplate template) throws ModelModificationException {
        templateService.updateNotificationTemplate(template);
    }

    @Override
    public void removeModel(NotificationTemplate template) throws ModelModificationException {
        templateService.removeNotificationTemplate(template);
    }

    @Override
    public void refreshAll() {
        dataProvider.refreshAll();
    }
}

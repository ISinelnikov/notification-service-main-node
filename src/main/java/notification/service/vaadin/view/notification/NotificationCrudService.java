package notification.service.vaadin.view.notification;

import notification.service.backend.repository.base.ModelModificationException;
import notification.service.backend.service.NotificationTemplateService;
import notification.service.domain.notification.rich.RichTemplate;
import notification.service.vaadin.common.CrudService;

import java.util.Objects;
import org.springframework.stereotype.Service;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

@Service
public class NotificationCrudService implements CrudService<RichTemplate> {
    private final NotificationTemplateService templateService;
    private final ListDataProvider<RichTemplate> dataProvider;

    public NotificationCrudService(NotificationTemplateService templateService) {
        this.templateService = Objects.requireNonNull(templateService, "Template service can't be null.");
        this.dataProvider = DataProvider.ofCollection(templateService
                .getAllNotificationTemplate());
    }

    public ListDataProvider<RichTemplate> getDataProvider() {
        return dataProvider;
    }

    @Override
    public void saveModel(RichTemplate template) throws ModelModificationException {
        templateService.addNotificationTemplate(template);
    }

    @Override
    public void updateModel(RichTemplate template) throws ModelModificationException {
        templateService.updateNotificationTemplate(template);
    }

    @Override
    public void removeModel(RichTemplate template) throws ModelModificationException {
        templateService.removeNotificationTemplate(template);
    }

    @Override
    public void refreshAll() {
        dataProvider.refreshAll();
    }
}

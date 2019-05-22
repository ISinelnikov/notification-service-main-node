package notification.service.vaadin.view.notification;

import notification.service.backend.repository.base.ModelModificationException;
import notification.service.domain.notification.rich.RichTemplate;
import notification.service.vaadin.MainLayout;
import notification.service.vaadin.common.DialogUtils;
import notification.service.vaadin.common.TextFieldUtils;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridContextMenu;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = NotificationView.VIEW_ROUTE, layout = MainLayout.class)
@PageTitle(NotificationView.VIEW_NAME)
public class NotificationView extends HorizontalLayout {
    private static final long serialVersionUID = -7751666562505228845L;

    public static final String VIEW_NAME = "Notification";
    public static final String VIEW_ROUTE = "notification";

    private final NotificationDialogService dialogService;
    private final NotificationGrid notificationGrid;
    private final NotificationCrudService notificationCrudService;
    private final ListDataProvider<RichTemplate> templates;

    public NotificationView(@Autowired NotificationDialogService dialogService,
            @Autowired NotificationCrudService notificationCrudService) {
        this.dialogService = Objects.requireNonNull(dialogService,
                "Notification template dialog service can't be null.");
        this.notificationCrudService = Objects.requireNonNull(notificationCrudService,
                "Notification crud service can't be null.");

        templates = notificationCrudService.getDataProvider();
        this.notificationGrid = new NotificationGrid();
        this.notificationGrid.setDataProvider(templates);

        initContextMenu();

        setSizeFull();
        VerticalLayout topLayout = createTopBar();

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(notificationGrid);
        barAndGridLayout.setFlexGrow(1, notificationGrid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(notificationGrid);

        add(barAndGridLayout);
    }

    private void initContextMenu() {
        GridContextMenu<RichTemplate> contextMenu = notificationGrid.addContextMenu();
        contextMenu.addItem("View", TextFieldUtils.getItemClickEvent(item ->
                item.ifPresent(dialogService::openExistNotificationTemplateReadOnly)));
        contextMenu.addItem("Edit", TextFieldUtils.getItemClickEvent(item ->
                item.ifPresent(dialogService::openExistNotificationTemplate)));
        contextMenu.addItem("Remove", TextFieldUtils.getItemClickEvent(item ->
                item.ifPresent(value ->
                        DialogUtils.showRemoveItemConfirmDialog((ComponentEventListener<ClickEvent<Button>>) event -> {
                            try {
                                notificationCrudService.removeModel(value);
                                notificationCrudService.refreshAll();
                            } catch (ModelModificationException e) {
                                Notification.show("Can't remove this item");
                            }
                        }))));
    }

    public VerticalLayout createTopBar() {
        return TextFieldUtils.createTopBar(VIEW_NAME, "Add template",
                event -> dialogService.openEmptyNotificationTemplate(),
                event -> {
                    String value = event.getValue();
                    templates.setFilter((SerializablePredicate<RichTemplate>) template ->
                            template.getTemplateTitle().contains(value) || template.getTemplateId().contains(value));
                });
    }

    public static class NotificationGrid extends Grid<RichTemplate> {
        private static final long serialVersionUID = -154566778461664742L;

        private static final String TEMPLATE_ID = "Template ID";
        private static final String TEMPLATE_TITLE = "Template title";
        private static final String INTERNAL_TITLE = "Internal title";

        public NotificationGrid() {
            setSizeFull();
            addColumn(RichTemplate::getTemplateId)
                    .setHeader(TEMPLATE_ID)
                    .setFlexGrow(5);
            addColumn(RichTemplate::getInternalTitle)
                    .setHeader(INTERNAL_TITLE)
                    .setFlexGrow(10)
                    .setSortable(true);
            addColumn(RichTemplate::getTemplateTitle)
                    .setHeader(TEMPLATE_TITLE)
                    .setFlexGrow(10)
                    .setSortable(true);
        }
    }
}

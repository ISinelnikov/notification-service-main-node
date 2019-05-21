package notification.service.vaadin.view.users;

import notification.service.backend.domain.SecurityEntity;
import notification.service.backend.domain.UserProfile;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.backend.service.SecurityNodeService;
import notification.service.vaadin.MainLayout;
import notification.service.vaadin.common.DialogUtils;
import notification.service.vaadin.common.TextFieldUtils;
import notification.service.vaadin.view.users.notification.SendingMassNotificationForm;
import notification.service.vaadin.view.users.notification.SendingNotificationForm;
import notification.service.vaadin.view.users.profile.UserProfileDialogService;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = UserView.VIEW_ROUTE, layout = MainLayout.class)
@PageTitle(UserView.VIEW_NAME)
public class UserView extends HorizontalLayout {
    private static final long serialVersionUID = 969217682378213090L;

    public static final String VIEW_NAME = "Users";
    public static final String VIEW_ROUTE = "users";

    private final SendingMassNotificationForm massNotificationForm;

    private final UsersDataProvider dataProvider;

    public UserView(@Autowired UsersDataProvider dataProvider,
            @Autowired SendingMassNotificationForm massNotificationForm,
            @Autowired SendingNotificationForm notificationForm,
            @Autowired SecurityNodeService nodeService,
            @Autowired UserProfileDialogService profileDialogService) {
        this.massNotificationForm = massNotificationForm;
        this.dataProvider = dataProvider;
        setSizeFull();
        VerticalLayout topLayout = createTopBar();

        UserGrid userGrid = new UserGrid(nodeService);
        userGrid.setDataProvider(dataProvider);

        GridContextMenu<UserProfile> usersGridContextMenu = userGrid.addContextMenu();
        usersGridContextMenu.addItem("Send notification", TextFieldUtils.getItemClickEvent(item ->
                item.ifPresent(notificationForm::open)));

        usersGridContextMenu.addItem("View profile", TextFieldUtils.getItemClickEvent(item ->
                item.ifPresent(profileDialogService::openUserProfileDialog)));

        usersGridContextMenu.addItem("Remove", TextFieldUtils.getItemClickEvent(item ->
                item.ifPresent(value ->
                        DialogUtils.showRemoveItemConfirmDialog((ComponentEventListener<ClickEvent<Button>>) event -> {
                            try {
                                dataProvider.removeUser(value);
                                dataProvider.refreshAll();
                            } catch (ModelModificationException e) {
                                Notification.show("Can't remove this item");
                            }
                        }))));

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(userGrid);
        barAndGridLayout.setFlexGrow(1, userGrid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(userGrid);

        add(barAndGridLayout);
    }

    public VerticalLayout createTopBar() {
        return TextFieldUtils.createTopBar(VIEW_NAME, "Mass mailing", event -> massNotificationForm.open(),
                event -> {
                    String eventValue = event.getValue();
                    dataProvider.setFilter((SerializablePredicate<UserProfile>) profile ->
                            profile.getNotificationId().contains(eventValue) || profile.getUserId().contains(eventValue));
                });
    }

    public static class UserGrid extends Grid<UserProfile> {
        private static final long serialVersionUID = 53916664399508477L;

        private static final String LAST_UPDATE_PROPERTY = "lastUpdate";
        private static final String DATE_TEMPLATE = "<div>[[item." + LAST_UPDATE_PROPERTY + "]]</div>";
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        private static final String USER_APPLICATION_PROPERTY = "applicationName";
        private static final String USER_APPLICATION_TEMPLATE = "<div>[[item." + USER_APPLICATION_PROPERTY + "]]</div>";

        public UserGrid(SecurityNodeService nodeService) {
            Objects.requireNonNull(nodeService, "Node service can't be null.");
            setSizeFull();
            addColumn(UserProfile::getUserId)
                    .setHeader("User ID")
                    .setFlexGrow(5);

            addColumn(UserProfile::getPackageName)
                    .setHeader("User package")
                    .setFlexGrow(10)
                    .setSortable(true);
            addColumn(UserProfile::getNotificationId)
                    .setHeader("User token")
                    .setFlexGrow(10);

            addColumn(UserProfile::getOs)
                    .setHeader("OS version")
                    .setFlexGrow(5);

            addColumn(UserProfile::getClickId)
                    .setHeader("Click id")
                    .setFlexGrow(5);

            addColumn(TemplateRenderer.<UserProfile>of(DATE_TEMPLATE)
                    .withProperty(LAST_UPDATE_PROPERTY, profile -> profile.getLastUpdate().format(TIME_FORMATTER)))
                    .setHeader("Last update")
                    .setFlexGrow(10)
                    .setComparator(Comparator.comparing(UserProfile::getLastUpdate));

            addColumn(TemplateRenderer.<UserProfile>of(USER_APPLICATION_TEMPLATE)
                    .withProperty(USER_APPLICATION_PROPERTY, profile -> {
                        SecurityEntity entity = nodeService.findSecurityEntity(profile.getNodeId());
                        return entity == null ? "N/A" : entity.getRowId();
                    }))
                    .setHeader("Node ID")
                    .setFlexGrow(5);
        }

        public UserProfile getSelectedRow() {
            return asSingleSelect().getValue();
        }
    }
}

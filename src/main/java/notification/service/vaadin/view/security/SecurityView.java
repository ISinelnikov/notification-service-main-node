package notification.service.vaadin.view.security;

import notification.service.backend.domain.SecurityEntity;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.vaadin.MainLayout;
import notification.service.vaadin.common.DialogUtils;
import notification.service.vaadin.common.TextFieldUtils;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.GridContextMenu;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = SecurityView.VIEW_ROUTE, layout = MainLayout.class)
@PageTitle(SecurityView.VIEW_NAME)
public class SecurityView extends HorizontalLayout {
    public static final String VIEW_NAME = "Nodes";
    public static final String VIEW_ROUTE = "nodes";

    private final SecurityGrid securityGrid;
    private final SecurityDialogService securityDialogService;
    private final SecurityCrudService securityCrudService;

    public SecurityView(@Autowired SecurityDialogService securityDialogService,
            @Autowired SecurityCrudService securityCrudService) {
        setSizeFull();

        this.securityCrudService = Objects.requireNonNull(securityCrudService,
                "Security crud service can't be null.");
        this.securityDialogService = Objects.requireNonNull(securityDialogService,
                "Security dialog service can't be null.");

        this.securityGrid = new SecurityGrid();
        this.securityGrid.setDataProvider(securityCrudService.getSecurityEntityDataProvider());
        addContextMenu();

        VerticalLayout topBar = createTopBar();

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topBar);
        barAndGridLayout.add(securityGrid);
        barAndGridLayout.setFlexGrow(1, securityGrid);
        barAndGridLayout.setFlexGrow(0, topBar);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(securityGrid);

        add(barAndGridLayout);
    }

    public VerticalLayout createTopBar() {
        return TextFieldUtils.createTopBar(VIEW_NAME, "Add node",
                (ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
                        securityDialogService.openEmptySecurityEntity(), null);
    }

    private void addContextMenu() {
        GridContextMenu<SecurityEntity> securityContextMenu = this.securityGrid.addContextMenu();
        securityContextMenu.addItem("Edit", TextFieldUtils.getItemClickEvent(item ->
                item.ifPresent(securityDialogService::openExistSecurityEntity)));
        securityContextMenu.addItem("Remove", TextFieldUtils.getItemClickEvent(item ->
                item.ifPresent(value ->
                        DialogUtils.showRemoveItemConfirmDialog((ComponentEventListener<ClickEvent<Button>>) event -> {
                            try {
                                securityCrudService.removeModel(value);
                                securityCrudService.refreshAll();
                            } catch (ModelModificationException e) {
                                Notification.show("Can't remove this item");
                            }
                        })
                )));
    }
}

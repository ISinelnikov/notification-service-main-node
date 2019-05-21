package notification.service.vaadin.view.users.profile;

import notification.service.backend.domain.UserProfile;
import notification.service.domain.sending.UserSendingHistoryRow;
import notification.service.domain.user.EventHistoryRow;
import notification.service.vaadin.common.DialogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.provider.DataProvider;

public class UserProfileWindow {
    private static final int DIALOG_WIDTH = 800;
    private static final String COMMON_GRID_HEIGHT = "600px";

    private final EventHistoryGrid eventHistoryGrid;
    private final SendingHistoryGrid sendingHistoryGrid;

    public UserProfileWindow(UserProfile profile,  List<EventHistoryRow> eventHistoryRows,
            List<UserSendingHistoryRow> sendingHistoryRows) {
        Dialog dialog = DialogUtils.initDialog(DIALOG_WIDTH);

        HorizontalLayout uuidLayout = new HorizontalLayout();
        uuidLayout.setSizeFull();

        uuidLayout.add(new Label("UUID:"), new Label(profile.getUserId()));

        eventHistoryGrid = new EventHistoryGrid();
        eventHistoryGrid.setHeight(COMMON_GRID_HEIGHT);
        eventHistoryGrid.setDataProvider(DataProvider.ofCollection(eventHistoryRows));
        eventHistoryGrid.setVisible(true);

        sendingHistoryGrid = new SendingHistoryGrid();
        sendingHistoryGrid.setHeight(COMMON_GRID_HEIGHT);
        sendingHistoryGrid.setDataProvider(DataProvider.ofCollection(sendingHistoryRows));
        sendingHistoryGrid.setVisible(false);

        dialog.add(uuidLayout, getTabs(), eventHistoryGrid, sendingHistoryGrid);
        dialog.open();
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.setSizeFull();
        Tab eventHistoryTab = new Tab("Event history");
        eventHistoryTab.setSelected(true);
        Tab sendingHistoryTab = new Tab("Sending history");

        Map<Tab, Grid> tabToLayout = new HashMap<>();

        tabs.add(eventHistoryTab, sendingHistoryTab);

        tabToLayout.put(eventHistoryTab, eventHistoryGrid);
        tabToLayout.put(sendingHistoryTab, sendingHistoryGrid);

        tabs.addSelectedChangeListener(event -> {
            tabToLayout.values().forEach(grid -> grid.setVisible(false));
            Tab selectedTab = tabs.getSelectedTab();
            tabToLayout.get(selectedTab).setVisible(true);
        });

        return tabs;
    }

    private static class EventHistoryGrid extends Grid<EventHistoryRow> {
        private static final long serialVersionUID = -8428830200127854509L;

        public EventHistoryGrid() {
            setSizeFull();

            addColumn(EventHistoryRow::getEventId)
                    .setHeader("Event ID")
                    .setFlexGrow(1);

            addColumn(EventHistoryRow::getEventUrl)
                    .setHeader("Event Url")
                    .setFlexGrow(4);

            addColumn(EventHistoryRow::getEventDate)
                    .setHeader("Event date")
                    .setFlexGrow(2);

            addColumn(EventHistoryRow::getEventCount)
                    .setHeader("Order")
                    .setFlexGrow(1);
        }
    }

    private static class SendingHistoryGrid extends Grid<UserSendingHistoryRow> {
        private static final long serialVersionUID = 1925538229599585874L;

        public SendingHistoryGrid() {
            setSizeFull();

            addColumn(UserSendingHistoryRow::getTemplateId)
                    .setHeader("Template ID")
                    .setFlexGrow(2);

            addColumn(UserSendingHistoryRow::getMessageId)
                    .setHeader("Message ID")
                    .setFlexGrow(2);

            addColumn(UserSendingHistoryRow::getSendingStatus)
                    .setHeader("Sending status")
                    .setFlexGrow(1);

            addColumn(UserSendingHistoryRow::getSendingTime)
                    .setHeader("Event date")
                    .setFlexGrow(2);
        }
    }
}

package notification.service.vaadin.view.users.profile;

import notification.service.backend.domain.UserProfile;
import notification.service.domain.sending.UserSendingHistoryRow;
import notification.service.vaadin.common.DialogUtils;

import java.util.List;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;

public class UserProfileWindow {
    private static final int DIALOG_WIDTH = 800;
    private static final String COMMON_GRID_HEIGHT = "600px";

    private final SendingHistoryGrid sendingHistoryGrid;

    public UserProfileWindow(UserProfile profile,
            List<UserSendingHistoryRow> sendingHistoryRows) {
        Dialog dialog = DialogUtils.initDialog(DIALOG_WIDTH);

        HorizontalLayout uuidLayout = new HorizontalLayout();
        uuidLayout.setSizeFull();

        uuidLayout.add(new Label("UUID:"), new Label(profile.getUserId()));

        sendingHistoryGrid = new SendingHistoryGrid();
        sendingHistoryGrid.setHeight(COMMON_GRID_HEIGHT);
        sendingHistoryGrid.setDataProvider(DataProvider.ofCollection(sendingHistoryRows));

        dialog.add(uuidLayout, sendingHistoryGrid);
        dialog.open();
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

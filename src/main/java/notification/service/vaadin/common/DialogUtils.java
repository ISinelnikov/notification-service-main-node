package notification.service.vaadin.common;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public final class DialogUtils {
    private static final String REMOVE_ITEM = "Do you want remove this item?";

    private DialogUtils() {
    }

    public static void showRemoveItemConfirmDialog( ComponentEventListener<ClickEvent<Button>> removeListener) {
        new DefaultConfirmDialog(REMOVE_ITEM, removeListener);
    }

    private static class DefaultConfirmDialog {
        public DefaultConfirmDialog(String content, ComponentEventListener<ClickEvent<Button>> confirmListener) {
            Dialog confirmDialog = new Dialog();

            VerticalLayout dialogLayout = new VerticalLayout();
            dialogLayout.setSizeFull();
            Label mainContent = new Label(content);

            dialogLayout.add(mainContent);

            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setSizeFull();

            Button confirm = new Button("Confirm", confirmListener);
            confirm.addClickListener(event -> confirmDialog.close());
            confirm.setSizeFull();
            confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            confirm.addClassName("confirm-remove-button");

            Button cancel = new Button("Cancel", event -> confirmDialog.close());
            cancel.setSizeFull();

            buttonLayout.add(cancel, confirm);

            dialogLayout.add(buttonLayout);

            confirmDialog.add(dialogLayout);
            confirmDialog.open();
        }
    }

    public static Dialog initDialog(int width) {
        Dialog dialog = new Dialog();
        dialog.setWidth(width + "px");

        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
        return dialog;
    }
}

package notification.service.vaadin.view.notification.window;

import notification.service.domain.notification.rich.RichTemplate;
import notification.service.vaadin.view.notification.window.input.MultiRichTextElement;
import notification.service.vaadin.view.notification.window.input.SimpleRichTextElement;

import java.util.Objects;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;

public class RichTemplateWindow extends VerticalLayout {
    private static final long serialVersionUID = -7445900450066975229L;

    protected static final String APPLICATION_TITLE = "Application title";
    protected static final String NOTIFICATION_TITLE = "Notification title";
    protected static final String NOTIFICATION_ICON = "Notification icon";
    protected static final String NOTIFICATION_BODY = "Notification body";
    protected static final String NOTIFICATION_IMAGE = "Notification image";

    protected SimpleRichTextElement notificationTitle;

    protected MultiRichTextElement notificationBody;

    protected Binder<RichTemplate> binder;

    public RichTemplateWindow(Binder<RichTemplate> binder) {
        this.binder = Objects.requireNonNull(binder, "Binder can't be null.");
        setSizeFull();
    }

    protected void setBean(RichTemplate bean) {
        binder.setBean(bean);
    }

    protected void blockAllFields() {
        notificationTitle.setReadOnly(true);
        notificationBody.setReadOnly(true);
    }

    protected void unblockAllFields() {
        notificationTitle.setReadOnly(false);
        notificationBody.setReadOnly(false);
    }

    protected boolean validate() {
        return binder.validate().isOk();
    }

    public RichTemplate getCurrentNotificationDto() {
        return binder.getBean();
    }
}

package notification.service.vaadin.view.notification.window;

import notification.service.domain.notification.BaseTemplate;
import notification.service.vaadin.view.notification.window.input.MultiRichTextElement;
import notification.service.vaadin.view.notification.window.input.SimpleRichTextElement;

import java.util.Objects;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import static notification.service.vaadin.common.TextFieldUtils.initSimpleTextFiledWithEmptyValidator;

public class BaseTemplateWindow<T extends BaseTemplate> extends VerticalLayout {
    private static final long serialVersionUID = -7445900450066975229L;

    protected static final String APPLICATION_TITLE = "Application title";
    protected static final String NOTIFICATION_TITLE = "Notification title";
    protected static final String NOTIFICATION_ICON = "Notification icon";
    protected static final String NOTIFICATION_BODY = "Notification body";
    protected static final String NOTIFICATION_IMAGE = "Notification image";

    protected SimpleRichTextElement applicationTitle;
    protected SimpleRichTextElement notificationTitle;

    protected TextField notificationIcon;

    protected MultiRichTextElement notificationBody;

    protected TextField notificationImage;

    protected Binder<T> binder;

    public BaseTemplateWindow(Binder<T> binder) {
        this.binder = Objects.requireNonNull(binder, "Binder can't be null.");
        setSizeFull();

        //Required not null
        applicationTitle = SimpleRichTextElement.initSimpleRichTextElement(APPLICATION_TITLE);
        add(applicationTitle);

        //Required not null
        notificationIcon = initSimpleTextFiledWithEmptyValidator(NOTIFICATION_ICON, binder,
                this, BaseTemplate::getIcon, BaseTemplate::setIcon);
    }

    protected void setBean(T bean) {
        binder.setBean(bean);
        applicationTitle.setCurrentElement(bean.getApplication());
    }

    protected void blockAllFields() {
        applicationTitle.setReadOnly(true);
        notificationTitle.setReadOnly(true);
        notificationIcon.setReadOnly(true);
        notificationBody.setReadOnly(true);
        notificationImage.setReadOnly(true);
    }

    protected void unblockAllFields() {
        applicationTitle.setReadOnly(false);
        notificationTitle.setReadOnly(false);
        notificationIcon.setReadOnly(false);
        notificationBody.setReadOnly(false);
        notificationImage.setReadOnly(false);
    }

    protected boolean validate() {
        return binder.validate().isOk() && applicationTitle.isValid();
    }

    public T getCurrentNotificationDto() {
        T bean = binder.getBean();
        bean.setApplication(applicationTitle.getCurrentState());
        return bean;
    }
}

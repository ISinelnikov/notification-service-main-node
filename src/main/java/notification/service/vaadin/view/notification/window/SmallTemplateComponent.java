package notification.service.vaadin.view.notification.window;

import notification.service.domain.notification.BaseTemplate;
import notification.service.domain.notification.firebase.SmallNotificationDto;
import notification.service.vaadin.view.notification.window.input.MultiRichTextElement;
import notification.service.vaadin.view.notification.window.input.SimpleRichTextElement;

import com.vaadin.flow.data.binder.Binder;

import static notification.service.vaadin.common.TextFieldUtils.initSimpleTextFiled;

public class SmallTemplateComponent extends BaseTemplateWindow<SmallNotificationDto> {
    private static final long serialVersionUID = 3812481638089210842L;

    public SmallTemplateComponent(SmallNotificationDto smallNotificationDto) {
        super(new Binder<>(SmallNotificationDto.class));

        notificationTitle = SimpleRichTextElement.initSimpleRichTextElement(NOTIFICATION_TITLE);

        notificationBody = MultiRichTextElement.initMultiRequiredRichTextElement(NOTIFICATION_BODY);

        add(notificationTitle, notificationBody);

        notificationImage = initSimpleTextFiled(NOTIFICATION_IMAGE, binder,
                this, BaseTemplate::getBodyImageUrl, BaseTemplate::setBodyImageUrl);

        setBean(smallNotificationDto);
        notificationTitle.setCurrentElement(smallNotificationDto.getTitle());
        notificationBody.setCurrentElements(smallNotificationDto.getBody());
    }

    @Override
    protected boolean validate() {
        return super.validate() && notificationBody.isValid();
    }

    @Override
    public SmallNotificationDto getCurrentNotificationDto() {
        SmallNotificationDto currentNotificationDto = super.getCurrentNotificationDto();
        currentNotificationDto.setTitle(notificationTitle.getCurrentState());
        currentNotificationDto.setBody(notificationBody.getCurrentState());
        return currentNotificationDto;
    }
}

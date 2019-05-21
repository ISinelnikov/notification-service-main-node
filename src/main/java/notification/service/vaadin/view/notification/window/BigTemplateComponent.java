package notification.service.vaadin.view.notification.window;

import notification.service.domain.notification.firebase.BigNotificationDto;
import notification.service.domain.notification.rich.component.RichButtonElement;
import notification.service.vaadin.component.RichTemplateButtonComponent;
import notification.service.vaadin.view.notification.window.input.MultiRichTextElement;
import notification.service.vaadin.view.notification.window.input.SimpleRichTextElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;

import static notification.service.domain.notification.rich.component.RichButtonElement.RichButtonPosition.LEFT;
import static notification.service.domain.notification.rich.component.RichButtonElement.RichButtonPosition.MIDDLE;
import static notification.service.domain.notification.rich.component.RichButtonElement.RichButtonPosition.RIGHT;
import static notification.service.vaadin.common.TextFieldUtils.initSimpleTextFiled;

public class BigTemplateComponent extends BaseTemplateWindow<BigNotificationDto> {
    private static final long serialVersionUID = -7038679901923888950L;

    private static final String NOTIFICATION_IMAGE = "Notification image";
    private static final String ERROR_MESSAGE = "Notification body or body image url can't be empty.";

    protected final List<RichTemplateButtonComponent> buttonTemplates = new ArrayList<>();

    public BigTemplateComponent(BigNotificationDto bigNotificationDto) {
        super(new Binder<>(BigNotificationDto.class));


        notificationTitle = SimpleRichTextElement.initSimpleRequiredRichTextElement(NOTIFICATION_TITLE);

        notificationBody = MultiRichTextElement.initMultiRichTextElement(NOTIFICATION_BODY);

        add(notificationTitle, notificationBody);

        notificationImage = initSimpleTextFiled(NOTIFICATION_IMAGE, binder, this,
                BigNotificationDto::getBodyImageUrl, BigNotificationDto::setBodyImageUrl);

        notificationBody.setErrorMessage(ERROR_MESSAGE);
        notificationImage.setErrorMessage(ERROR_MESSAGE);
        notificationBody.addValueChangeListener(event -> setInvalid(false));
        notificationImage.addValueChangeListener(event -> setInvalid(false));

        add(initRichButtons(bigNotificationDto.getButtons()));

        setBean(bigNotificationDto);
        System.out.println("BIG " + bigNotificationDto);
        notificationTitle.setCurrentElement(bigNotificationDto.getTitle());
        notificationBody.setCurrentElements(bigNotificationDto.getBody());
    }

    private void setInvalid(boolean value) {
        notificationBody.setInvalid(value);
        notificationImage.setInvalid(value);
    }

    @Override
    protected void blockAllFields() {
        super.blockAllFields();
        notificationImage.setReadOnly(true);
        buttonTemplates.forEach(RichTemplateButtonComponent::disableComponent);
    }

    @Override
    protected void unblockAllFields() {
        super.unblockAllFields();
        notificationImage.setReadOnly(false);
        buttonTemplates.forEach(RichTemplateButtonComponent::enableComponent);
    }

    @Override
    protected boolean validate() {
        boolean def = binder.validate().isOk();
        boolean requiredFieldsCheck = notificationBody.isValid() ||
                StringUtils.hasText(notificationImage.getValue());

        if (!requiredFieldsCheck) {
            setInvalid(true);
        }

        return def && requiredFieldsCheck;
    }

    /**
     * Set buttons values with positions.
     *
     * @param buttons current buttons.
     *
     * @return layout with buttons.
     */
    private HorizontalLayout initRichButtons(List<RichButtonElement> buttons) {
        HorizontalLayout richButtons = new HorizontalLayout();
        richButtons.setSizeFull();

        Map<RichButtonElement.RichButtonPosition, RichButtonElement> positionToButton = buttons.stream().collect(
                Collectors.toMap(RichButtonElement::getPosition, Function.identity()));

        RichButtonElement leftTemplateButton = positionToButton.get(LEFT);
        richButtons.add(getRichTemplateButton(LEFT, leftTemplateButton));

        RichButtonElement middleTemplateButton = positionToButton.get(MIDDLE);
        richButtons.add(getRichTemplateButton(MIDDLE, middleTemplateButton));

        RichButtonElement rightTemplateButton = positionToButton.get(RIGHT);
        richButtons.add(getRichTemplateButton(RIGHT, rightTemplateButton));

        return richButtons;
    }

    private RichTemplateButtonComponent getRichTemplateButton(RichButtonElement.RichButtonPosition buttonPosition,
            RichButtonElement templateButton) {
        RichTemplateButtonComponent buttonComponent = templateButton == null ?
                new RichTemplateButtonComponent(buttonPosition) : new RichTemplateButtonComponent(templateButton);
        buttonTemplates.add(buttonComponent);
        return buttonComponent;
    }

    private List<RichButtonElement> getCurrentButtonsState() {
        return buttonTemplates
                .stream()
                .filter(RichTemplateButtonComponent::isEnabled)
                .map(RichTemplateButtonComponent::getRichTemplateButton)
                .collect(Collectors.toList());
    }

    @Override
    public BigNotificationDto getCurrentNotificationDto() {
        BigNotificationDto currentBean = super.getCurrentNotificationDto();

        currentBean.setTitle(notificationTitle.getCurrentState());
        currentBean.setBody(notificationBody.getCurrentState());
        currentBean.setButtons(getCurrentButtonsState());

        System.out.println("After save: " + currentBean);
        return currentBean;
    }
}
package notification.service.vaadin.view.notification.window;

import notification.service.backend.repository.base.ModelModificationException;
import notification.service.domain.notification.firebase.NotificationTemplate;
import notification.service.domain.notification.firebase.base.RingtoneType;
import notification.service.domain.notification.firebase.base.SendingMode;
import notification.service.domain.notification.firebase.base.VibrationType;
import notification.service.vaadin.common.CrudService;
import notification.service.vaadin.common.DialogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import static notification.service.domain.notification.firebase.base.SendingMode.BIG_NOTIFICATION;
import static notification.service.domain.notification.firebase.base.SendingMode.SMALL_AND_BIG_NOTIFICATION;
import static notification.service.domain.notification.firebase.base.SendingMode.SMALL_NOTIFICATION;
import static notification.service.vaadin.common.TextFieldUtils.initSimpleTextFiled;
import static notification.service.vaadin.common.TextFieldUtils.initSimpleTextFiledWithEmptyValidator;

public class NotificationWindow {
    private final Dialog dialog;

    private static final String TEMPLATE_TITLE = "Template title";
    private static final String NOTIFICATION_URL = "Notification URL";
    private static final String TOP_IMAGE_URL = "Top image URL";
    private static final String VIBRATION_TYPE = "Vibration type";
    private static final String RINGTONE_TYPE = "Ringtone on/off";
    private static final String SENDING_MODE = "Sending mode";

    //-- Internal field
    private TextField templateTitle;

    private ComboBox<RingtoneType> ringtoneTypes;
    private ComboBox<VibrationType> vibrationTypes;

    private ComboBox<SendingMode> sendingMode;

    private SmallTemplateComponent smallTemplateComponent;
    private BigTemplateComponent bigTemplateComponent;

    private TextField bodyHref;
    private TextField topImageUrl;

    private Button confirm;

    private Tab smallNotification;
    private Tab bigNotification;

    private final Binder<NotificationTemplate> binder = new Binder<>(NotificationTemplate.class);

    public NotificationWindow(CrudService<NotificationTemplate> templateCrudService) {
        Objects.requireNonNull(templateCrudService, "Template crud service can't be null.");

        dialog = DialogUtils.initDialog(800);

        initMainLayout(new NotificationTemplate());

        confirm.addClickListener(event -> {
            boolean validateNotifications = validateNotifications();
            if (binder.validate().isOk() && validateNotifications) {
                blockAllFields();
                NotificationTemplate template = binder.getBean();
                template.setSmallNotification(smallTemplateComponent.getCurrentNotificationDto());
                template.setBigNotification(bigTemplateComponent.getCurrentNotificationDto());

                try {
                    templateCrudService.saveModel(template);
                    dialog.close();
                    templateCrudService.refreshAll();
                } catch (ModelModificationException e) {
                    Notification.show("Can't save template with error.");
                } finally {
                    unblockAllFields();
                }
            }
        });

        dialog.open();
    }

    private boolean validateNotifications() {
        boolean smallTemplateIsValid = true;
        boolean bigTemplateIsValid = true;

        SendingMode currentMode = sendingMode.getValue();
        if (currentMode == SMALL_NOTIFICATION || currentMode == SMALL_AND_BIG_NOTIFICATION) {
            smallTemplateIsValid = smallTemplateComponent.validate();
        }
        if (currentMode == BIG_NOTIFICATION || currentMode == SMALL_AND_BIG_NOTIFICATION) {
            bigTemplateIsValid = bigTemplateComponent.validate();
        }

        return smallTemplateIsValid && bigTemplateIsValid;
    }

    public NotificationWindow(CrudService<NotificationTemplate> templateCrudService,
            NotificationTemplate template, boolean isEditable) {
        Objects.requireNonNull(templateCrudService, "Template crud service can't be null.");
        Objects.requireNonNull(template, "Template can't be null.");

        dialog = DialogUtils.initDialog(800);
        initMainLayout(template);

        if (isEditable) {
            confirm.addClickListener(event -> {
                boolean validateNotifications = validateNotifications();
                if (binder.validate().isOk() && validateNotifications) {
                    blockAllFields();

                    NotificationTemplate updatedTemplate = binder.getBean();
                    updatedTemplate.setSmallNotification(smallTemplateComponent.getCurrentNotificationDto());
                    updatedTemplate.setBigNotification(bigTemplateComponent.getCurrentNotificationDto());
                    try {
                        //updatedTemplate.setButtons(getCurrentButtonsState());
                        templateCrudService.updateModel(updatedTemplate);
                        dialog.close();
                        templateCrudService.refreshAll();
                    } catch (ModelModificationException e) {
                        Notification.show("Can't update template with error.");
                    } finally {
                        unblockAllFields();
                    }
                }
            });
        } else {
            blockAllFields();
        }

        dialog.open();
    }

    private void blockAllFields() {
        templateTitle.setReadOnly(true);
        bodyHref.setReadOnly(true);
        topImageUrl.setReadOnly(true);

        ringtoneTypes.setEnabled(false);
        vibrationTypes.setEnabled(false);
        sendingMode.setEnabled(false);
        confirm.setEnabled(false);

        smallTemplateComponent.blockAllFields();
        bigTemplateComponent.blockAllFields();
    }

    private void unblockAllFields() {
        templateTitle.setReadOnly(false);
        bodyHref.setReadOnly(false);
        topImageUrl.setReadOnly(false);

        ringtoneTypes.setEnabled(true);
        vibrationTypes.setEnabled(true);
        sendingMode.setEnabled(true);
        confirm.setEnabled(true);

        smallTemplateComponent.unblockAllFields();
        bigTemplateComponent.unblockAllFields();
    }

    private void initMainLayout(NotificationTemplate template) {
        binder.setBean(template);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        templateTitle = initSimpleTextFiledWithEmptyValidator(TEMPLATE_TITLE, binder, mainLayout,
                NotificationTemplate::getTemplateTitle, NotificationTemplate::setTemplateTitle);
        templateTitle.setMaxLength(256);

        HorizontalLayout hrefAndImage = new HorizontalLayout();
        hrefAndImage.setSizeFull();

        bodyHref = initSimpleTextFiledWithEmptyValidator(NOTIFICATION_URL, binder, hrefAndImage,
                NotificationTemplate::getNotificationUrl, NotificationTemplate::setNotificationUrl);

        topImageUrl = initSimpleTextFiled(TOP_IMAGE_URL, binder, hrefAndImage,
                NotificationTemplate::getTopImageUrl, NotificationTemplate::setTopImageUrl);

        mainLayout.add(hrefAndImage);

        HorizontalLayout vibrationAndRingtone = new HorizontalLayout();
        vibrationAndRingtone.setSizeFull();

        vibrationTypes = new ComboBox<>(VIBRATION_TYPE);
        vibrationTypes.setSizeFull();
        vibrationTypes.setItems(VibrationType.getAllVibrationTypes());
        vibrationTypes.setItemLabelGenerator(VibrationType::getAlias);

        vibrationTypes.setValue(template.getVibrationType());

        //Support nullable values
        vibrationTypes.addValueChangeListener(event ->
                template.setVibrationType(vibrationTypes.getValue()));

        vibrationAndRingtone.add(vibrationTypes);

        ringtoneTypes = new ComboBox<>(RINGTONE_TYPE);
        ringtoneTypes.setSizeFull();
        ringtoneTypes.setItems(RingtoneType.getAllRingtoneTypes());
        ringtoneTypes.setItemLabelGenerator(RingtoneType::getDescription);
        ringtoneTypes.setAllowCustomValue(false);

        if (template.getRingtoneType() != null) {
            ringtoneTypes.setValue(template.getRingtoneType());
        } else {
            ringtoneTypes.setValue(RingtoneType.SOUND_DISABLE);
            template.setRingtoneType(RingtoneType.SOUND_DISABLE);
        }
        ringtoneTypes.addValueChangeListener(event -> {
            if (ringtoneTypes.getValue() == null) {
                ringtoneTypes.setValue(RingtoneType.SOUND_DISABLE);
            } else {
                template.setRingtoneType(ringtoneTypes.getValue());
            }
        });

        sendingMode = new ComboBox<>(SENDING_MODE);
        sendingMode.setSizeFull();
        sendingMode.setItems(SendingMode.getAllModes());
        sendingMode.setItemLabelGenerator(SendingMode::getDescription);
        sendingMode.setAllowCustomValue(false);

        sendingMode.setValue(template.getSendingMode());

        sendingMode.addValueChangeListener(event -> {
            if (sendingMode.getValue() == null) {
                sendingMode.setValue(SMALL_NOTIFICATION);
                template.setSendingMode(SMALL_NOTIFICATION);
            } else {
                template.setSendingMode(sendingMode.getValue());
            }
        });

        vibrationAndRingtone.add(ringtoneTypes);

        mainLayout.add(vibrationAndRingtone);
        mainLayout.add(sendingMode);

        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        smallTemplateComponent = new SmallTemplateComponent(template.getSmallNotification());
        bigTemplateComponent = new BigTemplateComponent(template.getBigNotification());
        bigTemplateComponent.setVisible(false);

        mainLayout.add(getTabs(), smallTemplateComponent, bigTemplateComponent, getButtonLayout());

        dialog.add(mainLayout);
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.setSizeFull();
        smallNotification = new Tab("Small notification");
        smallNotification.setSelected(true);
        bigNotification = new Tab("Big notification");

        Map<Tab, VerticalLayout> tabToLayout = new HashMap<>();

        tabs.add(smallNotification, bigNotification);

        tabToLayout.put(smallNotification, smallTemplateComponent);
        tabToLayout.put(bigNotification, bigTemplateComponent);

        tabs.addSelectedChangeListener(event -> {
            tabToLayout.values().forEach(verticalLayout -> verticalLayout.setVisible(false));
            Tab selectedTab = tabs.getSelectedTab();
            tabToLayout.get(selectedTab).setVisible(true);
        });

        return tabs;
    }

    private HorizontalLayout getButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSizeFull();

        confirm = new Button("Confirm");
        confirm.setSizeFull();
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.setSizeFull();
        buttonLayout.add(cancelButton, confirm);
        return buttonLayout;
    }
}

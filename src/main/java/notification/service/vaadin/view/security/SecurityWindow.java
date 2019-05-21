package notification.service.vaadin.view.security;

import notification.service.backend.domain.SecurityEntity;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.vaadin.common.DialogUtils;

import java.util.UUID;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import static notification.service.vaadin.common.TextFieldUtils.initSimpleTextFiled;
import static notification.service.vaadin.common.TextFieldUtils.initSimpleTextFiledWithEmptyValidator;

public class SecurityWindow {
    private static final String APPLICATION_NAME = "Application name";

    private static final String CONNECTION_TYPE = "Connection type";
    private static final String IP_ADDRESS = "Ip address";

    private static final String DOMAIN = "Domain";
    private static final String PORT = "Port";

    private static final String SECURITY_TYPE = "Application type";

    private final Binder<SecurityEntity> binder = new Binder<>(SecurityEntity.class);

    private final Dialog mainDialog;

    private TextField applicationNameField;
    private TextField securityTokenField;

    private ComboBox<SecurityEntity.ConnectionType> connectionType;
    private TextField ipAddressField;

    private TextField domain;

    private ComboBox<SecurityEntity.SecurityType> securityType;

    private Button confirm;
    private Button generateSecurityToken;

    private final SecurityEntity entity;

    public SecurityWindow(SecurityCrudService securityCrudService) {
        mainDialog = DialogUtils.initDialog(400);
        entity = new SecurityEntity();
        initialFields();

        confirm.addClickListener(event -> {
           if (binder.validate().isOk()) {
                blockAllFields();
                SecurityEntity entity = binder.getBean();
                try {
                    securityCrudService.saveModel(entity);
                    mainDialog.close();
                    securityCrudService.refreshAll();
                } catch (ModelModificationException e) {
                    Notification.show("Can't save template with error.");
                } finally {
                    unblockAllFields();
                }
            }
        });

        mainDialog.open();
    }

    public SecurityWindow(SecurityCrudService securityCrudService,
            SecurityEntity existEntity, boolean isEditable) {
        mainDialog = DialogUtils.initDialog(400);
        this.entity = existEntity;
        initialFields();

        if (isEditable) {
            confirm.addClickListener(event -> {
                if (binder.validate().isOk()) {
                    blockAllFields();
                    SecurityEntity entity = binder.getBean();
                    try {
                        securityCrudService.updateModel(entity);
                        mainDialog.close();
                        securityCrudService.refreshAll();
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

        mainDialog.open();
    }

    private void blockAllFields() {
        applicationNameField.setReadOnly(true);
        ipAddressField.setReadOnly(true);
        securityType.setReadOnly(true);
        domain.setReadOnly(true);
        confirm.setEnabled(false);
        generateSecurityToken.setEnabled(false);
    }

    private void unblockAllFields() {
        applicationNameField.setReadOnly(false);
        ipAddressField.setReadOnly(false);
        securityType.setReadOnly(false);
        domain.setReadOnly(false);
        confirm.setEnabled(true);
        generateSecurityToken.setEnabled(true);
    }

    private void initialFields() {
        binder.setBean(entity);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        applicationNameField = initSimpleTextFiledWithEmptyValidator(APPLICATION_NAME, binder, mainLayout,
                SecurityEntity::getApplicationName, SecurityEntity::setApplicationName);
        applicationNameField.setMaxLength(256);

        connectionType = new ComboBox<>(CONNECTION_TYPE);
        connectionType.setWidth("35%");
        connectionType.setRequired(true);
        connectionType.setAllowCustomValue(false);

        connectionType.setItemLabelGenerator(SecurityEntity.ConnectionType::getDescription);
        connectionType.setItems(SecurityEntity.ConnectionType.getEnabledTypes());
        connectionType.setValue(entity.getConnectionType() != null ?
                entity.getConnectionType() :
                SecurityEntity.ConnectionType.HTTP);
        entity.setConnectionType(connectionType.getValue());

        connectionType.addValueChangeListener(event -> {
            SecurityEntity.ConnectionType currentType = connectionType.getValue();
            if (currentType == null) {
                connectionType.setValue(SecurityEntity.ConnectionType.HTTP);
            } else {
                entity.setConnectionType(currentType);
            }
        });

        HorizontalLayout addressLayout = new HorizontalLayout();
        addressLayout.setSizeFull();

        addressLayout.add(connectionType);

        ipAddressField = initSimpleTextFiledWithEmptyValidator(IP_ADDRESS, binder, addressLayout,
                SecurityEntity::getIpAddress, SecurityEntity::setIpAddress);
        ipAddressField.setMaxLength(256);
        ipAddressField.setWidth("35%");

        TextField port = initSimpleTextFiledWithEmptyValidator(PORT, binder, addressLayout,
                SecurityEntity::getPort, SecurityEntity::setPort);
        port.setMaxLength(7);
        port.setWidth("21%");

        mainLayout.add(addressLayout);

        domain = initSimpleTextFiled(DOMAIN, binder, mainLayout,
                SecurityEntity::getDomain, SecurityEntity::setDomain);

        mainLayout.add(new Label("Security token:"));

        mainLayout.add(initGenerateTokenLayout());

        securityType = new ComboBox<>(SECURITY_TYPE);
        securityType.setRequired(true);
        securityType.setAllowCustomValue(false);
        securityType.setSizeFull();

        securityType.setItemLabelGenerator(SecurityEntity.SecurityType::getDescription);

        securityType.setItems(SecurityEntity.SecurityType.getEnabledTypes());
        securityType.setValue(entity.getSecurityType() != null ?
                entity.getSecurityType() :
                SecurityEntity.SecurityType.SITE_NODE);
        entity.setSecurityType(securityType.getValue());

        securityType.addValueChangeListener(event -> {
            SecurityEntity.SecurityType currentType = securityType.getValue();
            if (currentType == null) {
                securityType.setValue(SecurityEntity.SecurityType.SITE_NODE);
            } else {
                entity.setSecurityType(currentType);
            }
        });

        mainLayout.add(securityType);

        mainLayout.add(initButtonLayout());

        mainDialog.add(mainLayout);
    }

    private HorizontalLayout initGenerateTokenLayout() {
        securityTokenField = new TextField();
        securityTokenField.setReadOnly(true);

        // Generate default value
        if (entity.getSecurityToken() == null) {
            generateTokenValue();
        } else {
            setTokenValue(entity.getSecurityToken());
        }

        generateSecurityToken = new Button("Update");
        generateSecurityToken.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout tokenLayout = new HorizontalLayout();
        tokenLayout.setWidth("100%");
        tokenLayout.add(securityTokenField);
        tokenLayout.add(generateSecurityToken);
        tokenLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START, securityTokenField);
        tokenLayout.expand(securityTokenField);

        generateSecurityToken.addClickListener(event -> {
            generateTokenValue();
        });

        return tokenLayout;
    }

    private void setTokenValue(String token) {
        securityTokenField.setReadOnly(false);
        securityTokenField.setValue(token);
        securityTokenField.setReadOnly(true);
    }

    private void generateTokenValue() {
        setTokenValue(UUID.randomUUID().toString());
        entity.setSecurityToken(securityTokenField.getValue());
    }

    private HorizontalLayout initButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSizeFull();

        confirm = new Button("Confirm");
        confirm.setSizeFull();
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", event -> mainDialog.close());
        cancelButton.setSizeFull();
        buttonLayout.add(cancelButton, confirm);
        return buttonLayout;
    }
}

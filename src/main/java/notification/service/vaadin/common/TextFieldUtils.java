package notification.service.vaadin.common;

import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.util.StringUtils;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.GridContextMenu;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;

public final class TextFieldUtils {
    private TextFieldUtils() {
    }

    public static <T> ComponentEventListener<GridContextMenu
            .GridContextMenuItemClickEvent<T>> getItemClickEvent(ContextMenuItemClickEventConsumer<T> consumer) {
        return (ComponentEventListener<GridContextMenu.GridContextMenuItemClickEvent<T>>)
                contextMenuItemClickEvent -> consumer.accept(contextMenuItemClickEvent.getItem());
    }

    public static TextField getRequiredTextField(String title) {
        return getTextField(title, true);
    }

    public static TextField getNotRequiredTextField(String title) {
        return getTextField(title,false);
    }

    private static TextField getTextField(String title, boolean required) {
        TextField customTextField = new TextField(title);
        customTextField.setPlaceholder(title);
        customTextField.setRequired(required);
        customTextField.setSizeFull();
        return customTextField;
    }

    public static TextArea getTextArea(String title) {
        TextArea customTextArea = new TextArea(title);
        customTextArea.setPlaceholder(title);
        customTextArea.setSizeFull();
        customTextArea.setHeight("120px");
        customTextArea.setMaxLength(512);
        return customTextArea;
    }

    public static VerticalLayout createTopBar(String barTitle, String appendButtonTitle,
            ComponentEventListener<ClickEvent<Button>> appendButtonEvent,
            @Nullable HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextField, String>> filterEvent) {
        TextField filter = new TextField();
        filter.setEnabled(true);

        if (filterEvent != null) {
            filter.addValueChangeListener(filterEvent);
        } else {
            filter.setEnabled(false);
        }

        Button appendButton = new Button(appendButtonTitle);
        appendButton.addClickListener(appendButtonEvent);
        appendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout topBarLayout = new VerticalLayout();
        topBarLayout.setWidth("100%");

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(appendButton);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START, filter);
        topLayout.expand(filter);

        topBarLayout.add(new H6(barTitle), topLayout);
        return topBarLayout;
    }

    public static <T> TextField initSimpleTextFiledWithEmptyValidator(String field, Binder<T> binder, HasComponents rootComponent,
            ValueProvider<T, String> getter, Setter<T, String> setter) {
        TextField customField = getRequiredTextField(field);
        binder.forField(customField)
                .withValidator(StringUtils::hasText, ValidatorUtils.emptyError(field))
                .bind(getter, setter);
        rootComponent.add(customField);
        return customField;
    }

    public static <T> TextField initSimpleTextFiled(String field, Binder<T> binder, HasComponents rootComponent,
            ValueProvider<T, String> getter, Setter<T, String> setter) {
        TextField customField = getNotRequiredTextField(field);
        binder.forField(customField)
                .bind(getter, setter);
        rootComponent.add(customField);
        return customField;
    }

    public static <T> TextArea initSimpleAreaFiled(String field, Binder<T> binder, HasComponents rootComponent,
            ValueProvider<T, String> getter, Setter<T, String> setter) {
        TextArea customField = getTextArea(field);
        binder.forField(customField)
                .bind(getter, setter);
        rootComponent.add(customField);
        return customField;
    }

    public static <T> TextArea initSimpleAreaFiledWithEmptyValidator(String field, Binder<T> binder, HasComponents rootComponent,
            ValueProvider<T, String> getter, Setter<T, String> setter) {
        TextArea customField = getTextArea(field);
        customField.setRequired(true);
        binder.forField(customField)
                .withValidator(StringUtils::hasText, ValidatorUtils.emptyError(field))
                .bind(getter, setter);
        rootComponent.add(customField);
        return customField;
    }

    @FunctionalInterface
    public interface ContextMenuItemClickEventConsumer<T> {
        void accept(Optional<T> value);
    }
}

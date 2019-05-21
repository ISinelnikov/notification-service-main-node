package notification.service.vaadin.view.notification.window.input;

import notification.service.domain.notification.rich.component.RichTextElement;
import notification.service.utils.JsonUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import com.vaadin.flow.component.textfield.TextArea;

public class MultiRichTextElement extends TextArea {
    private static final long serialVersionUID = 497541279205078833L;

    private MultiRichTextElement(String title, boolean isRequired) {
        super(title);
        setRequired(isRequired);
        setSizeFull();
    }

    public static MultiRichTextElement initMultiRichTextElement(String title) {
        return new MultiRichTextElement(title, false);
    }

    public static MultiRichTextElement initMultiRequiredRichTextElement(String title) {
        return new MultiRichTextElement(title, true);
    }

    public void setCurrentElements(@Nullable List<RichTextElement> elements) {
        if (elements != null) {
            String collect = elements.stream().map(RichTextElement::getText).collect(Collectors.joining());
            this.setValue(collect);
        }
    }

    @Nullable
    public List<RichTextElement> getCurrentState() {
        String stringTrimOrNull = JsonUtils.getStringTrimOrNull(this.getValue());

        if (stringTrimOrNull == null) {
            return null;
        }

        return Collections.singletonList(new RichTextElement(stringTrimOrNull));
    }

    public boolean isValid() {
        return !isRequired() || validateTemplate();
    }

    private boolean validateTemplate() {
        return true;
    }
}

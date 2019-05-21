package notification.service.vaadin.view.notification.window.input;

import notification.service.domain.notification.rich.component.RichTextElement;
import notification.service.utils.JsonUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import com.vaadin.flow.component.textfield.TextField;

import static notification.service.domain.notification.rich.component.RichTextElement.TextProperty.BOLD;
import static notification.service.domain.notification.rich.component.RichTextElement.TextProperty.COLOR;
import static notification.service.domain.notification.rich.component.RichTextElement.TextProperty.ITALIC;

public class SimpleRichTextElement extends TextField {
    private static final long serialVersionUID = -1394723678256405882L;

    private SimpleRichTextElement(String title, boolean isRequired) {
        super(title);
        setRequired(isRequired);
        setSizeFull();
    }

    public static SimpleRichTextElement initSimpleRichTextElement(String title) {
        return new SimpleRichTextElement(title, false);
    }

    public static SimpleRichTextElement initSimpleRequiredRichTextElement(String title) {
        return new SimpleRichTextElement(title, true);
    }

    public void setCurrentElement(@Nullable RichTextElement element) {
        if (element != null) {
            this.setValue(convertToString(element));
        }
    }

    public boolean isValid() {
        if (isRequired())
            return validateTemplate();
        return true;
    }

    private boolean validateTemplate() {
        String element = this.getValue();
        if (element.trim().length() == 0) {
            return false;
        }

        return element.contains("<![") && element.contains("]>") && element.contains("</>");
    }

    @Nullable
    public RichTextElement getCurrentState() {
        String currentTemplate = this.getValue();

        String stringTrimOrNull = JsonUtils.getStringTrimOrNull(currentTemplate);
        if (stringTrimOrNull == null) {
            return null;
        }

        return convertToTemplate(currentTemplate);
    }

    @Nullable
    public static RichTextElement convertToTemplate(String element) {
        if (element.trim().length() == 0) {
            return null;
        }

        if (!element.contains("<![") || !element.contains("]>") || !element.contains("</>")) {
            return null;
        }

        int indexOfOpenTag = element.indexOf("]>") + 2;

        int indexOfCloseTag = element.indexOf("</>");

        String mainText = element.substring(indexOfOpenTag, indexOfCloseTag);

        String attributesString = element.substring(3, indexOfOpenTag - 2);
        String[] attributesArray = attributesString.split(", ");

        Map<String, Object> attributes = Stream.of(attributesArray)
                .map(attribute -> {
                    String[] split = attribute.split("=");
                    if (split.length != 2) {
                        return null;
                    }
                    String key = split[0];
                    Object value;
                    if (key.equals("color")) {
                        value = split[1];
                    } else {
                        value = split[1].equals("true");
                    }
                    return new AbstractMap.SimpleEntry<>(key, value);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        RichTextElement textElement = new RichTextElement(mainText, attributes);

        return textElement;
    }

    public static String convertToString(RichTextElement element) {
        StringBuilder elementText = new StringBuilder();

        elementText.append("<!");

        if (element.getAttribute() != null) {
            List<String> attributes = new ArrayList<>();

            boolean containsBold = element.getAttribute()
                    .containsKey(BOLD.getAlias());

            if (containsBold) {
                attributes.add(BOLD.getAlias() + "=" + element.getAttribute().get(BOLD.getAlias()));
            }

            boolean containsItalic = element.getAttribute()
                    .containsKey(ITALIC.getAlias());

            if (containsItalic) {
                attributes.add(ITALIC.getAlias() + "=" + element.getAttribute().get(ITALIC.getAlias()));
            }

            boolean containsColor = element.getAttribute()
                    .containsKey(COLOR.getAlias());

            if (containsColor) {
                attributes.add(COLOR.getAlias() + "=" + element.getAttribute().get(COLOR.getAlias()));
            }

            elementText.append(attributes.toString());
        }

        elementText.append(">");

        elementText.append(element.getText());

        elementText.append("</>");

        return elementText.toString();
    }
}

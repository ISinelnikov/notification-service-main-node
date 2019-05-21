package notification.service.backend.repository.notification.service.vaadin.view.notification.window.input;

import notification.service.domain.notification.rich.component.RichTextElement;
import notification.service.vaadin.view.notification.window.input.SimpleRichTextElement;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static notification.service.domain.notification.rich.component.RichTextElement.TextProperty.BOLD;
import static notification.service.domain.notification.rich.component.RichTextElement.TextProperty.COLOR;
import static notification.service.domain.notification.rich.component.RichTextElement.TextProperty.ITALIC;

public class SimpleRichTextElementTest {
    private static final Logger logger = LoggerFactory.getLogger(SimpleRichTextElementTest.class);

    @Test
    public void convertToStringTest() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(BOLD.getAlias(), true);
        attributes.put(ITALIC.getAlias(), false);
        attributes.put(COLOR.getAlias(), "#ffff00");

        RichTextElement testElementWithAttributes = new RichTextElement("Test text", attributes);

        logger.debug("Current element: '{}'.", testElementWithAttributes);
        logger.debug("Result: '{}'.", SimpleRichTextElement.convertToString(testElementWithAttributes));

        RichTextElement textElementWithEmptyAttributes = new RichTextElement("Test test", new HashMap<>());

        logger.debug("Current element: '{}'.", textElementWithEmptyAttributes);
        logger.debug("Result: '{}'.", SimpleRichTextElement.convertToString(textElementWithEmptyAttributes));
    }

    @Test
    public void convertToTemplateTest() {
        String element = "<![bold=true, italic=false, color=#ffff00]>Test text</>";

        logger.debug("Result: {}. ", SimpleRichTextElement.convertToTemplate(element));
    }
}

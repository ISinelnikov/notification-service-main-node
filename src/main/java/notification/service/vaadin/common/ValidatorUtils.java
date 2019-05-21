package notification.service.vaadin.common;

public final class ValidatorUtils {
    private ValidatorUtils() {
    }

    public static String emptyError(String field) {
        return "Field '" + field + "' can't be empty.";
    }
}
